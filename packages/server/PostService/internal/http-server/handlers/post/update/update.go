package update

import (
	"context"
	"errors"
	"io"
	"log/slog"
	"net/http"
	"regexp"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"github.com/go-playground/validator/v10"
	"go.mongodb.org/mongo-driver/bson/primitive"

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
	HeaderImage string   `json:"header_image,omitempty"`
	Tags        []string `json:"tags,omitempty"`
}

var URLRegex = regexp.MustCompile(`^(https?://[^\s/$.?#].[^\s]*)$`)

// PostUpdater is an interface that defines the method for updating a post.
// Update takes a context, postId, title, content, header image, and tags, and returns an error if the update fails.
type PostUpdater interface {
	Update(
		ctx context.Context,
		postId primitive.ObjectID,
		title string,
		content string,
		headerImage string,
		tags []string,
	) error
}

func urlIfContentNotEmpty(fl validator.FieldLevel) bool {
	headerImage := fl.Field().String()
	content := fl.Parent().FieldByName("Content").String()

	if content != "" {
		return URLRegex.MatchString(headerImage)
	}
	return true
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
// @Success 200 {object} map[string]interface{} "Success message"
// @Failure 400 {object} map[string]interface{} "Validation errors or request decoding failures"
// @Router /api/posts/{id} [patch]
func New(log *slog.Logger, postUpdater PostUpdater) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.update.New"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		id := chi.URLParam(r, "id")
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

		if req.Title == "" && req.Content == "" && req.HeaderImage == "" && len(req.Tags) == 0 {
			log.Error("no fields provided for update")

			render.JSON(w, r, resp.Error(
				map[string][]string{"body": {"At least one field must be provided"}},
				http.StatusBadRequest,
			))
			return
		}

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

		postId, err := primitive.ObjectIDFromHex(id)
		if err != nil {
			log.Error("failed to create objectId from postId", sl.Err(err))

			render.JSON(w, r, resp.Error(
				map[string][]string{"postId": {"Invalid postId format"}},
				http.StatusBadRequest,
			))
			return
		}

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

		log.Info("post updated", slog.Any("id", id))

		render.JSON(w, r, map[string]interface{}{
			"Status":  http.StatusOK,
			"Message": "Successfully updated",
		})
	}
}
