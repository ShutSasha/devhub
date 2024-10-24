package update

import (
	"context"
	"errors"
	"io"
	"net/http"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/render"
	"github.com/go-playground/validator/v10"
	"go.mongodb.org/mongo-driver/bson/primitive"

	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
)

type Request struct {
	Title       string   `json:"title,omitempty"`
	Description string   `json:"description,omitempty"`
	HeaderImage string   `json:"header_image,omitempty"`
	Tags        []string `json:"tags,omitempty"`
}

type PostUpdater interface {
	Update(
		ctx context.Context,
		postId primitive.ObjectID,
		title string,
		description string,
		headerImage string,
		tags []string,
	) error
}

func New(postUpdater PostUpdater) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.update.New"

		id := chi.URLParam(r, "id")
		var req Request

		err := render.DecodeJSON(r.Body, &req)
		if errors.Is(err, io.EOF) {
			render.JSON(w, r, resp.Error(
				map[string][]string{"body": {"Empty request"}},
				http.StatusBadRequest,
			))
			return
		}
		if err != nil {
			render.JSON(w, r, resp.Error(
				map[string][]string{"body": {"Failed to decode request"}},
				http.StatusBadRequest,
			))

			return
		}

		if err := validator.New().Struct(req); err != nil {
			validateErr := err.(validator.ValidationErrors)

			render.JSON(w, r, resp.ValidationError(
				validateErr,
				http.StatusBadRequest,
			))

			return
		}

		postId, err := primitive.ObjectIDFromHex(id)
		if err != nil {
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
			req.Description,
			req.HeaderImage,
			req.Tags,
		)
		if err != nil {
			render.JSON(w, r, resp.Error(
				map[string][]string{"post": {err.Error()}},
				http.StatusBadRequest,
			))

			return
		}

		render.JSON(w, r, map[string]interface{}{
			"Status":  http.StatusOK,
			"Message": "Successfully updated",
		})
	}
}
