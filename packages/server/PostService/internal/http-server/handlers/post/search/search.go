package search

import (
	"context"
	"log/slog"
	"net/http"
	"strconv"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/interfaces"

	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/sl"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
)

// New handles the search for posts with optional query parameters like sorting, pagination, and tags.
//
// @Summary      Search for posts
// @Description  "asc" - from oldest to newest, "desc" - from newest to oldest. Default is "desc".
// @Tags         posts
// @Accept       json
// @Produce      json
// @Param        q       query   string  false  "Search query for post title or content" example("Golang")
// @Param        tags[]   query   []string false  "Tags to filter posts" collectionFormat(multi) example("dev","tech")
// @Param        sort    query   string  false  "Sort order for posts by date" Enums(asc, desc) default(desc)
// @Param        page    query   int     false  "Page number for pagination" default(1)
// @Param        limit   query   int     false  "Number of posts per page" default(10)
// @Success      200     {array} storage.PostModel "List of posts"
// @Failure      400     {object} map[string][]string "Invalid pagination or query parameters"
// @Failure      404     {object} map[string]interface{} "No posts found"
// @Failure      500     {object} map[string][]string "Internal server error"
// @Router       /api/posts/search [get]
func New(log *slog.Logger, postSearcher interfaces.PostSearcher, fileProvider interfaces.FileProvider) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.search.New"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		sortBy := r.URL.Query().Get("sort")
		query := r.URL.Query().Get("q")
		tags := r.URL.Query()["tags[]"]

		pageStr := r.URL.Query().Get("page")
		limitStr := r.URL.Query().Get("limit")

		page := 1
		limit := 10
		var err error

		if pageStr != "" {
			page, err = strconv.Atoi(pageStr)
			if err != nil || page < 1 {
				render.JSON(w, r, resp.Error(map[string][]string{
					"pagination": {"Invalid page number"},
				}, http.StatusBadRequest))
				return
			}
		}

		if limitStr != "" {
			limit, err = strconv.Atoi(limitStr)
			if err != nil || limit < 1 {
				render.JSON(w, r, resp.Error(map[string][]string{
					"pagination": {"Invalid limit number"},
				}, http.StatusBadRequest))
				return
			}
		}
		sortOrder := parseSortParam(sortBy)
		posts, _, err := postSearcher.Search(
			context.TODO(),
			sortOrder,
			query,
			tags,
			fileProvider,
			page,
			limit,
		)
		if err != nil {
			log.Error("can not get posts", sl.Err(err))

			render.JSON(w, r, resp.Error(map[string][]string{
				"posts": {err.Error()},
			}, http.StatusInternalServerError))
			return
		}
		if posts == nil {
			render.JSON(w, r, map[string]interface{}{})
			return
		}

		log.Info("posts successfully found")

		render.JSON(w, r, posts)
	}
}

func parseSortParam(sort string) int {
	if sort == "asc" {
		return 1
	}

	return -1
}
