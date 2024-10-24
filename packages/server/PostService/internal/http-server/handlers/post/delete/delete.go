package delete

import (
	"context"
	"net/http"

	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/render"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type PostRemover interface {
	Delete(
		ctx context.Context,
		postId primitive.ObjectID,
	) error
}

func New(postRemover PostRemover) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.delete.New"

		id := chi.URLParam(r, "id")

		postId, err := primitive.ObjectIDFromHex(id)
		if err != nil {
			render.JSON(w, r, resp.Error(
				map[string][]string{"postId": {"Invalid postId format"}},
				http.StatusBadRequest,
			))

			return
		}
		err = postRemover.Delete(
			context.TODO(),
			postId,
		)
		if err != nil {
			render.JSON(w, r, resp.Error(map[string][]string{
				"post": {err.Error()},
			}, http.StatusInternalServerError))

			return
		}

		render.JSON(w, r, map[string]interface{}{
			"Status":  http.StatusOK,
			"Message": "Successfully deleted",
		})
	}
}
