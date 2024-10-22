package get

import (
	"context"
	"errors"
	"net/http"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/models"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage"

	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/render"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type PostProvider interface {
	GetPostById(
		ctx context.Context,
		postId primitive.ObjectID,
	) (*models.Post, error)
}

func New(postProvider PostProvider) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.get.New"

		id := chi.URLParam(r, "id")

		postId, err := primitive.ObjectIDFromHex(id)
		if err != nil {
			render.JSON(w, r, resp.Error(err.Error(), http.StatusBadRequest))

			return
		}
		post, err := postProvider.GetPostById(
			context.TODO(),
			postId,
		)
		if errors.Is(err, storage.ErrPostNotFound) {
			render.JSON(w, r, resp.Error("not found", http.StatusNotFound))
		}
		if err != nil {

			render.JSON(w, r, resp.Error(storage.ErrPostNotFound.Error(), http.StatusInternalServerError))

			return
		}

		render.JSON(w, r, post)
	}
}
