package tags

import (
	"log/slog"
	"net/http"
	"strconv"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/interfaces"
	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
)

// GetPopularTags is a handler function that retrieves a list of popular tags.
// It calls the TagsProvider to get tags sorted by popularity.
// @Summary Get popular tags
// @Description This endpoint retrieves a list of the most popular tags used in posts.
// @Tags tags
// @Accept json
// @Produce json
// @Param limit query int false "Limit the number of tags returned" default(10)
// @Success 200 {array} string "List of popular tags"
// @Failure 500 {object} map[string]interface{} "Internal server error"
// @Router /api/posts/get-popular-tags [get]
func GetPopularTags(log *slog.Logger, tagsProvider interfaces.TagsProvider) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.tags.get_popular_tags"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		limit := 10
		if limitStr := r.URL.Query().Get("limit"); limitStr != "" {
			if l, err := strconv.Atoi(limitStr); err == nil && l > 0 {
				limit = l
			}
		}

		tags, err := tagsProvider.GetPopularTags(r.Context(), limit)
		if err != nil {
			log.Error("failed to fetch popular tags", slog.Any("error", err))
			render.JSON(w, r, resp.Error(map[string][]string{
				"error": {"Can't get tags"},
			}, http.StatusInternalServerError))
			return
		}

		render.JSON(w, r, tags)
	}
}
