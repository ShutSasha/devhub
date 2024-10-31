package search

import (
	"context"
	"errors"
	"log/slog"
	"net/http"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage"

	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/sl"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
)

// PostSearcher is an interface that defines the method for retrieving a posts by query.
// Search takes a context, sorting method, search query, tags and returns the posts array or an error.
type PostSearcher interface {
	Search(
		ctx context.Context,
		sortBy string,
		query string,
		tags []string,
	) ([]storage.PostModel, error)
}

// New is a handler function that processes the HTTP request to retrieve posts by query.
// It extracts the query, tags and sort method from the URL, validates it, and calls the PostSearcher to get the post.
// @Summary Get posts by query
// @Description This endpoint retrieves posts by query.
// @Tags posts
// @Accept json
// @Produce json
// @Success 200 {object} []models.Post "The requested posts"
// @Failure 404 {object} map[string]interface{} "Posts not found"
// @Failure 500 {object} map[string]interface{} "Internal server error"
// @Router /api/posts/search [get]
func New(log *slog.Logger, postSearcher PostSearcher) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.search.New"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		sortBy := r.URL.Query().Get("sort")
		query := r.URL.Query().Get("q")
		tags := r.URL.Query()["tags[]"]

		posts, err := postSearcher.Search(
			context.TODO(),
			sortBy,
			query,
			tags,
		)
		if errors.Is(err, storage.ErrPostsNotFound) {
			log.Error(storage.ErrPostsNotFound.Error())

			render.JSON(w, r, resp.Error(map[string][]string{
				"posts": {storage.ErrPostsNotFound.Error()},
			}, http.StatusNotFound))
			return
		}
		if err != nil {
			log.Error("can not get posts", sl.Err(err))

			render.JSON(w, r, resp.Error(map[string][]string{
				"posts": {err.Error()},
			}, http.StatusInternalServerError))
			return
		}

		log.Info("posts successfully found")

		render.JSON(w, r, posts)
	}
}
