package single

import (
	"context"
	"errors"
	"log/slog"
	"net/http"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/interfaces"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/utils"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

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
func New(log *slog.Logger, postProvider interfaces.PostProvider, fileProvider interfaces.FileProvider) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.get.single.New"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		id := chi.URLParam(r, "id")

		postId, err := primitive.ObjectIDFromHex(id)
		if err != nil {
			utils.HandleError(log, w, r, "failed to create objectId from postId", err, http.StatusBadRequest, "post", storage.ErrPostNotFound.Error())
			return
		}

		post, err := postProvider.GetById(context.TODO(), postId, fileProvider)
		if errors.Is(err, storage.ErrPostNotFound) {
			utils.HandleError(log, w, r, storage.ErrPostNotFound.Error(), nil, http.StatusNotFound, "post", storage.ErrPostNotFound.Error())
			return
		}
		if err != nil {
			utils.HandleError(log, w, r, "can not get post", err, http.StatusInternalServerError, "post", err.Error())
			return
		}

		log.Info("post successfully found", slog.Any("id", postId))

		render.JSON(w, r, post)
	}
}
