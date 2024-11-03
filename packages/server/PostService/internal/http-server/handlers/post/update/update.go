package update

import (
	"context"
	"log/slog"
	"net/http"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"github.com/go-playground/validator/v10"
	"go.mongodb.org/mongo-driver/bson/primitive"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/interfaces"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/utils"
	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/sl"
)

// Request struct defines the fields allowed for updating a post.
// - Title: The title of the post (optional, between 1 and 128 characters).
// - Content: The content of the post (optional, max 62792 characters).
// - HeaderImage: Optional image URL to update the post header.
// - Tags: Optional list of tags associated with the post.
type Request struct {
	Title       string   `json:"title,omitempty" validate:"omitempty,max=128,min=1"`
	Content     string   `json:"content,omitempty" validate:"omitempty,min=1,max=62792"`
	HeaderImage string   `json:"headerImage,omitempty"`
	Tags        []string `json:"tags,omitempty"`
}

// New is a handler function that processes the HTTP request for updating a post.
// It validates the request body, checks for errors, and calls the PostUpdater to update the post.
// @Summary Update an existing post
// @Description This endpoint allows a user to update an existing post with a new title, content, header image, and tags.
// @Tags posts
// @Accept json
// @Produce json
// @Param id path string true "Post ID"
// @Param request body Request true "Update post request body"
// @Success 200 {object} map[string]interface{} "Model of the updated post"
// @Failure 400 {object} map[string]interface{} "Validation errors or request decoding failures"
// @Router /api/posts/{id} [patch]
func New(log *slog.Logger, postUpdater interfaces.PostUpdater, postProvider interfaces.PostProvider,
	fileSaver interfaces.FileSaver, fileRemover interfaces.FileRemover, fileProvider interfaces.FileProvider) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.update.New"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		if err := r.ParseMultipartForm(10 << 20); err != nil {
			utils.HandleError(log, w, r, "failed to parse multipart form", err, http.StatusInternalServerError, "headerImage", "Failed to parse multipart form")
			return
		}

		id := chi.URLParam(r, "id")
		postId, err := primitive.ObjectIDFromHex(id)
		if err != nil {
			utils.HandleError(log, w, r, "failed to create objectId from postId", err, http.StatusBadRequest, "postId", "Invalid postId format")
			return
		}

		req := Request{
			Title:   r.FormValue("title"),
			Content: r.FormValue("content"),
			Tags:    r.Form["tags"],
		}
		if err := validator.New().Struct(req); err != nil {
			validateErr := err.(validator.ValidationErrors)
			utils.HandleValidatorError(log, w, r, "invalid request", err, validateErr, http.StatusBadRequest)
			return
		}

		post, err := postProvider.GetById(context.TODO(), postId, fileProvider)
		if err != nil {
			utils.HandleError(log, w, r, "failed to get post by id", err, http.StatusInternalServerError, "post", err.Error())
			return
		}

		if _, _, err := r.FormFile("headerImage"); err == nil {
			fileRemoveErr := fileRemover.Remove(context.TODO(), post.HeaderImage)
			if fileRemoveErr != nil {
				log.Error("failed to delete post image from aws", sl.Err(fileRemoveErr))
			} else {
				log.Info("header image successfuly deleted from aws")
			}
		} else if err != http.ErrMissingFile {
			utils.HandleError(log, w, r, "error retrieving file", err, http.StatusBadRequest, "headerImage", "Failed to retrieve file")
			return
		}

		_, _, err = r.FormFile("headerImage")
		if err != nil {
			if err != http.ErrMissingFile {
				utils.HandleError(log, w, r, "failed to retrieve file", err, http.StatusBadRequest, "headerImage", "Failed to retrieve file")
				return
			}
		} else {
			newImageKey, err := utils.HandleFileUpload(log, r, post.User.Id, fileSaver)
			if err != nil {
				utils.HandleError(log, w, r, "file upload error", err, http.StatusBadRequest, "headerImage", "Failed to retrieve or save file")
				return
			}
			log.Info("new post image successfuly saved to aws")

			err = fileRemover.Remove(context.TODO(), post.HeaderImage)
			if err != nil {
				log.Error("failed to delete previous post image from aws", sl.Err(err))
			} else {
				log.Info("previous post image successfuly deleted from aws")
			}

			req.HeaderImage = newImageKey
		}

		if req.Title == "" && req.Content == "" && len(req.Tags) == 0 && req.HeaderImage == "" {
			utils.HandleError(log, w, r, "no fields provided for update", nil, http.StatusBadRequest, "body", "At least one field must be provided")
			return
		}

		//Todo proceed case when not updated (upload back old header image)
		err = postUpdater.Update(
			context.TODO(),
			postId,
			req.Title,
			req.Content,
			req.HeaderImage,
			req.Tags,
		)
		if err != nil {
			log.Error("failed to update post", sl.Err(err))

			render.JSON(w, r, resp.Error(
				map[string][]string{"post": {err.Error()}},
				http.StatusBadRequest,
			))

			return
		}

		updatedPost, err := postProvider.GetById(context.TODO(), postId, fileProvider)
		if err != nil {
			utils.HandleError(log, w, r, "failed to retrieve post", err, http.StatusInternalServerError, "postId", "Failed to retrieve the updated post")
			return
		}

		log.Info("post updated", slog.Any("id", id))

		render.JSON(w, r, updatedPost)
	}
}
