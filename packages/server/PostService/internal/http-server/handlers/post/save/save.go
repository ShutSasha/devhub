package save

import (
	"context"
	"fmt"
	"log/slog"
	"net/http"
	"strings"

	pb "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/gen/go/user"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/interfaces"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/utils"
	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/sl"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"github.com/go-playground/validator/v10"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

// Request struct defines the JSON request body for the handler.
// - UserId: The ID of the user creating the post (required).
// - Title: The title of the post (required, between 1 and 128 characters).
// - Content: The content of the post (required, max 62792 characters).
// - HeaderImage: The image at the top of the post.
// - Tags: Optional tags associated with the post.
type Request struct {
	UserId      string   `json:"userId" validate:"required"`
	Title       string   `json:"title" validate:"required,max=128,min=1"`
	Content     string   `json:"content" validate:"required,max=62792"`
	HeaderImage string   `json:"headerImage,omitempty"`
	Tags        []string `json:"tags,omitempty"`
}

// @Summary Save a new post
// @Description This endpoint allows a user to save a new post with a title, content, optional header image, and tags.
// @Tags posts
// @Accept multipart/form-data
// @Produce json
// @Param userId formData string true "User ID of the user creating the post"
// @Param title formData string true "Title of the post"
// @Param content formData string true "Content of the post"
// @Param headerImage formData file false "Header image for the post"
// @Param tags formData string false "Optional tags associated with the post (e.g., [tag1,tag2])"
// @Success 200 {object} map[string]interface{} "Returns the details of the newly created post, including post ID, title, content, header image key, and tags"
// @Failure 400 {object} map[string]interface{} "Validation errors, request decoding failures, or file upload errors"
// @Failure 500 {object} map[string]interface{} "Internal server error"
// @Router /api/posts [post]
func New(
	log *slog.Logger,
	postSaver interfaces.PostSaver,
	postProvider interfaces.PostProvider,
	postRemover interfaces.PostRemover,
	fileSaver interfaces.FileSaver,
	fileProvider interfaces.FileProvider,
	fileRemover interfaces.FileRemover,
	grpcClient pb.UserServiceClient,
) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.save.New"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		if err := r.ParseMultipartForm(10 << 20); err != nil {
			utils.HandleError(log, w, r, "failed to parse multipart form", err, http.StatusBadRequest, "headerImage", "Failed to parse multipart form")
			return
		}

		tagsString := r.FormValue("tags")

		cleanedString := strings.Trim(tagsString, "[]")
		tagsArray := strings.Split(cleanedString, ",")

		for i := range tagsArray {
			tagsArray[i] = strings.TrimSpace(tagsArray[i])
		}

		req := Request{
			UserId:  r.FormValue("userId"),
			Title:   r.FormValue("title"),
			Content: r.FormValue("content"),
			Tags:    tagsArray,
		}
		if err := validator.New().Struct(req); err != nil {
			validateErr := err.(validator.ValidationErrors)
			utils.HandleValidatorError(log, w, r, "invalid request", err, validateErr, http.StatusBadRequest)
			return
		}

		userId, err := primitive.ObjectIDFromHex(req.UserId)
		if err != nil {
			utils.HandleError(log, w, r, "failed to create objectId from req.UserId", err, http.StatusBadRequest, "userId", "Invalid userId format")
			return
		}

		var imageKey string
		if _, _, fileErr := r.FormFile("headerImage"); fileErr == nil {
			imageKey, err = utils.HandleFileUpload(log, r, userId, fileSaver)
			if err != nil {
				utils.HandleError(log, w, r, "file upload error", err, http.StatusBadRequest, "headerImage", "Failed to retrieve or save file")
				return
			}
		} else if fileErr != http.ErrMissingFile {
			utils.HandleError(log, w, r, "error retrieving file", fileErr, http.StatusBadRequest, "headerImage", "Failed to retrieve file")
			return
		}

		id, err := postSaver.Save(context.TODO(), userId, req.Title, req.Content, imageKey, req.Tags)
		if err != nil {
			utils.HandleError(log, w, r, "failed to add post", err, http.StatusBadRequest, "userId", err.Error())
			return
		}

		if err := notifyUserService(log, grpcClient, req.UserId, id, postRemover, fileRemover,
			imageKey, w, r); err != nil {
			return
		}

		post, err := postProvider.GetById(context.TODO(), id, fileProvider)
		if err != nil {
			utils.HandleError(log, w, r, "failed to retrieve post", err, http.StatusInternalServerError, "postId", "Failed to retrieve the newly created post")
			return
		}

		log.Info("post successfully added")
		render.JSON(w, r, post)
	}
}

func notifyUserService(
	log *slog.Logger,
	grpcClient pb.UserServiceClient,
	userId string,
	postId primitive.ObjectID,
	postRemover interfaces.PostRemover,
	fileRemover interfaces.FileRemover,
	imageKey string,
	w http.ResponseWriter,
	r *http.Request,
) error {
	grpcAddResponse, err := grpcClient.AddPostToUser(context.TODO(), &pb.AddPostRequest{
		UserId: userId,
		PostId: postId.Hex(),
	})
	if err != nil || !grpcAddResponse.Success {
		message := "failed to notify user service"
		if grpcAddResponse != nil {
			message = "User service returned failure: " + grpcAddResponse.Message
		}
		if err != nil {
			handlePostDeletionAndError(log, postRemover, fileRemover, imageKey, w, r, err, message, postId)
		} else {
			handlePostDeletionAndError(log, postRemover, fileRemover, imageKey, w, r, fmt.Errorf(grpcAddResponse.Message), message, postId)
		}
		return err
	}
	return nil
}

func handlePostDeletionAndError(
	log *slog.Logger,
	postRemover interfaces.PostRemover,
	fileRemover interfaces.FileRemover,
	key string,
	w http.ResponseWriter,
	r *http.Request,
	err error,
	message string,
	id primitive.ObjectID,
) {
	log.Error(message, sl.Err(err))
	log.Info("deleting post", slog.Any("id", id))

	err = postRemover.Remove(context.TODO(), id)
	if err != nil {
		log.Error("can not delete post", sl.Err(err))
	} else {
		log.Info("post successfully deleted", slog.Any("id", id))

		err = fileRemover.Remove(context.TODO(), key)
		if err != nil {
			log.Error("can not delete post's header image", sl.Err(err))
		} else {
			log.Info("post's header image successfully deleted")
		}
	}

	render.JSON(w, r, resp.Error(
		map[string][]string{"userService": {message}},
		http.StatusInternalServerError,
	))
}
