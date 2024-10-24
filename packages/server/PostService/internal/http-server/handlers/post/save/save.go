package save

import (
	"context"
	"errors"
	"io"
	"net/http"

	"github.com/go-chi/render"
	"github.com/go-playground/validator/v10"
	"go.mongodb.org/mongo-driver/bson/primitive"

	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
)

type Request struct {
	UserId      string   `json:"userId" validate:"required"`
	Title       string   `json:"title" validate:"required,max=128,min=1"`
	Description string   `json:"description" validate:"required,max=62792"`
	Tags        []string `json:"tags,omitempty"`
}

type PostSaver interface {
	SavePost(
		ctx context.Context,
		userId primitive.ObjectID,
		title string,
		description string,
		tags []string,
	) (primitive.ObjectID, error)
}

func New(postSaver PostSaver) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.save.New"

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

		userId, err := primitive.ObjectIDFromHex(req.UserId)
		if err != nil {
			render.JSON(w, r, resp.Error(
				map[string][]string{"userId": {"Invalid userId format"}},
				http.StatusBadRequest,
			))
			return
		}

		id, err := postSaver.SavePost(
			context.TODO(),
			userId,
			req.Title,
			req.Description,
			req.Tags,
		)
		if err != nil {
			render.JSON(w, r, resp.Error(
				map[string][]string{"userId": {err.Error()}},
				http.StatusBadRequest,
			))

			return
		}

		render.JSON(w, r, map[string]interface{}{
			"_id": id,
		})
	}
}
