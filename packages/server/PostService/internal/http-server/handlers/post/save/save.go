package save

import (
	"context"
	"errors"
	"io"
	"log/slog"
	"net/http"
	"regexp"

	pb "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/gen/go/user"
	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/sl"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"github.com/go-playground/validator/v10"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

var URLRegex = regexp.MustCompile(`^(https?://[^\s/$.?#].[^\s]*)$`)

// Request struct defines the JSON request body for the handler.
// - UserId: The ID of the user creating the post (required).
// - Title: The title of the post (required, between 1 and 128 characters).
// - Content: The content of the post (required, max 62792 characters).
// - HeaderImage: The image at the top of the post.
// - Tags: Optional tags associated with the post.
type Request struct {
	UserId      string   `json:"user" validate:"required"`
	Title       string   `json:"title" validate:"required,max=128,min=1"`
	Content     string   `json:"content" validate:"required,max=62792"`
	HeaderImage string   `json:"header_image,omitempty"`
	Tags        []string `json:"tags,omitempty"`
}

func urlIfContentNotEmpty(fl validator.FieldLevel) bool {
	headerImage := fl.Field().String()
	content := fl.Parent().FieldByName("Content").String()

	if content != "" {
		return URLRegex.MatchString(headerImage)
	}
	return true
}

// PostSaver is an interface defining the method to save a post.
// SavePost takes a context, user ID, title, content, and tags,
// and returns the ID of the saved post or an error.
type PostSaver interface {
	Save(
		ctx context.Context,
		userId primitive.ObjectID,
		title string,
		content string,
		headerImage string,
		tags []string,
	) (primitive.ObjectID, error)
}

// PostRemover is an interface that defines the method for deleting a post by its ID.
// Delete takes a context and postId, and returns an error if the deletion fails.
type PostRemover interface {
	Delete(
		ctx context.Context,
		postId primitive.ObjectID,
	) error
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
	postSaver PostSaver,
	postRemover PostRemover,
	grpcClient pb.UserServiceClient,
) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.save.New"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		var req Request

		err := render.DecodeJSON(r.Body, &req)
		if errors.Is(err, io.EOF) {
			log.Error("request body is empty")

			render.JSON(w, r, resp.Error(
				map[string][]string{"body": {"Empty request"}},
				http.StatusBadRequest,
			))
			return
		}
		if err != nil {
			log.Error("failed to decode request body", sl.Err(err))

			render.JSON(w, r, resp.Error(
				map[string][]string{"body": {"Failed to decode request"}},
				http.StatusBadRequest,
			))

			return
		}

		log.Info("request body decoded", slog.Any("request", req))

		validate := validator.New()
		validate.RegisterValidation("url_if_content_not_empty", urlIfContentNotEmpty)

		if err := validator.New().Struct(req); err != nil {
			validateErr := err.(validator.ValidationErrors)

			log.Error("invalid request", sl.Err(err))

			render.JSON(w, r, resp.ValidationError(
				validateErr,
				http.StatusBadRequest,
			))

			return
		}

		userId, err := primitive.ObjectIDFromHex(req.UserId)
		if err != nil {
			log.Error("failed to create objectId from req.UserId", sl.Err(err))

			render.JSON(w, r, resp.Error(
				map[string][]string{"userId": {"Invalid userId format"}},
				http.StatusBadRequest,
			))
			return
		}

		id, err := postSaver.Save(
			context.TODO(),
			userId,
			req.Title,
			req.Content,
			req.HeaderImage,
			req.Tags,
		)
		if err != nil {
			log.Error("failed to add post", sl.Err(err))

			render.JSON(w, r, resp.Error(
				map[string][]string{"userId": {err.Error()}},
				http.StatusBadRequest,
			))

			return
		}

		log.Info("post added", slog.Any("id", id))

		grpcAddResponse, err := grpcClient.AddPostToUser(context.TODO(), &pb.AddPostRequest{
			UserId: req.UserId,
			PostId: id.Hex(),
		})
		if err != nil {
			log.Error("failed to notify user service", sl.Err(err))

			log.Info("deleting post", slog.Any("id", id))

			err = postRemover.Delete(context.TODO(), id)
			if err != nil {
				log.Error("can not delete post", sl.Err(err))
			} else {
				log.Info("post successfully deleted", slog.Any("id", id))
			}

			render.JSON(w, r, resp.Error(
				map[string][]string{"userService": {"Failed to notify user service"}},
				http.StatusInternalServerError,
			))
			return
		}
		if !grpcAddResponse.Success {
			log.Error("user service returned failure", slog.String("message", grpcAddResponse.Message))

			// TODO: refactor
			log.Info("deleting post", slog.Any("id", id))

			err = postRemover.Delete(context.TODO(), id)
			if err != nil {
				log.Error("can not delete post", sl.Err(err))
			} else {
				log.Info("post successfully deleted", slog.Any("id", id))
			}

			render.JSON(w, r, resp.Error(
				map[string][]string{"userService": {"User service returned failure: " + grpcAddResponse.Message}},
				http.StatusInternalServerError,
			))
			return
		}

		log.Info("post succesfully added to user")

		render.JSON(w, r, map[string]interface{}{
			"_id": id,
		})
	}
}
