package paginate

import (
	"context"
	"log/slog"
	"net/http"
	"strconv"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/get"
	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/sl"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
)

// @Summary      Get Paginated Posts
// @Description  Retrieves a paginated list of posts with optional limit and page query parameters.
// @Tags         posts
// @Accept       json
// @Produce      json
// @Param        limit  query  int     false  "Number of posts per page (default is 10)"  minimum(1)
// @Param        page   query  int     false  "Page number for pagination (default is 1)" minimum(1)
// @Success      200    {array}  storage.PostModel  "List of paginated posts"
// @Failure      500    {object}  error        "Internal Server Error"
// @Router       /api/posts [get]
func New(log *slog.Logger, postProvider get.PostProvider) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.get.paginate.New"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		limit, err := strconv.Atoi(r.URL.Query().Get("limit"))
		if err != nil || limit <= 0 {
			log.Info("invalid limit parameter, defaulting to 10", slog.Any("limit", limit))
			limit = 10
		}

		page, err := strconv.Atoi(r.URL.Query().Get("page"))
		if err != nil || page <= 0 {
			log.Info("invalid page parameter, defaulting to 1", slog.Any("page", page))
			page = 1
		}

		posts, err := postProvider.GetPaginated(context.TODO(), limit, page)
		if err != nil {
			log.Error("failed to get paginated posts", sl.Err(err))
			render.JSON(w, r, resp.Error(map[string][]string{
				"posts": {err.Error()},
			}, http.StatusInternalServerError))
			return
		}

		log.Info("posts successfully retrieved")
		if len(posts) == 0 {
			render.JSON(w, r, map[string]interface{}{})
			return
		}

		render.JSON(w, r, posts)
	}
}
