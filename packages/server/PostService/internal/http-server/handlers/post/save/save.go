package save

import (
	"context"
	"fmt"
	"log/slog"
	"net/http"

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

// TODO Make validation for max 4 tags
type Request struct {
	UserId      string   `json:"userId" validate:"required"`
	Title       string   `json:"title" validate:"required,max=128,min=1"`
	Content     string   `json:"content" validate:"required,max=62792"`
	HeaderImage string   `json:"headerImage,omitempty"`
	Tags        []string `json:"tags,omitempty"`
}

// New is a handler function that processes the HTTP request for saving a post.
// It validates the incoming request body, checks for errors, and if valid,
// calls the SavePost method of the PostSaver interface to persist the post.
// @Summary Save a new post
// @Description This endpoint allows a user to save a new post with a title, content, and optional tags.
// @Tags posts
// @Accept json
// @Produce json
// @Param request body Request true "Post save request body"
// @Success 200 {object} map[string]interface{} "Returns the ID of the newly created post"
// @Failure 400 {object} map[string]interface{} "Validation errors or request decoding failures"
// @Router /api/posts [post]
func New(
	log *slog.Logger,
	postSaver interfaces.PostSaver,
	postRemover interfaces.PostRemover,
	fileSaver interfaces.FileSaver,
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

		req := Request{
			UserId:  r.FormValue("userId"),
			Title:   r.FormValue("title"),
			Content: r.FormValue("content"),
			Tags:    r.Form["tags"],
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

		log.Info("post successfully added")
		render.JSON(w, r, map[string]interface{}{"_id": id})
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
