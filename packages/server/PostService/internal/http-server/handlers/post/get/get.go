package get

import (
	"context"
	"errors"
	"log/slog"
	"net/http"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage"

	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/sl"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

// PostProvider is an interface that defines the method for retrieving a post by its ID.
// GetPostById takes a context and postId, and returns the post model or an error.
type PostProvider interface {
	GetById(
		ctx context.Context,
		postId primitive.ObjectID,
	) (*storage.PostModel, error)
}

// New is a handler function that processes the HTTP request to retrieve a post by its ID.
// It extracts the post ID from the URL, validates it, and calls the PostProvider to get the post.
// @Summary Get post by ID
// @Description This endpoint retrieves a post by its unique ID.
// @Tags posts
// @Accept json
// @Produce json
// @Param id path string true "Post ID"
// @Success 200 {object} models.Post "The requested post"
// @Failure 400 {object} map[string]interface{} "Invalid postId format"
// @Failure 404 {object} map[string]interface{} "Post not found"
// @Failure 500 {object} map[string]interface{} "Internal server error"
// @Router /api/posts/{id} [get]
func New(log *slog.Logger, postProvider PostProvider) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.get.New"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		id := chi.URLParam(r, "id")

		postId, err := primitive.ObjectIDFromHex(id)
		if err != nil {
			log.Error("failed to create objectId from postId", sl.Err(err))

			render.JSON(w, r, resp.Error(
				map[string][]string{"postId": {"Invalid postId format"}},
				http.StatusBadRequest,
			))

			return
		}

		post, err := postProvider.GetById(
			context.TODO(),
			postId,
		)
		if errors.Is(err, storage.ErrPostNotFound) {
			log.Error(storage.ErrPostNotFound.Error(), slog.Any("post", postId))

			render.JSON(w, r, resp.Error(map[string][]string{
				"post": {storage.ErrPostNotFound.Error()},
			}, http.StatusNotFound))
			return
		}
		if err != nil {
			log.Error("can not get post", sl.Err(err))

			render.JSON(w, r, resp.Error(map[string][]string{
				"post": {err.Error()},
			}, http.StatusInternalServerError))
			return
		}

		log.Info("post successfully found", slog.Any("id", postId))

		render.JSON(w, r, post)
	}
}
