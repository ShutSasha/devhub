package http

import (
	"context"
	"log/slog"
	"net/http"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/utils"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
)

// @Summary      Retrieve a comment by ID
// @Description  Fetch a comment using its unique ID.
// @Tags         comments
// @Accept       json
// @Produce      json
// @Param        id path string true "Comment ID"
// @Success      200 {object} map[string]interface{} "Comment retrieved successfully"
// @Failure      404 {object} map[string]interface{} "Comment not found"
// @Failure      500 {object} map[string]interface{} "Failed to retrieve comment"
// @Router       /api/comments/{id} [get]
func (ch *CommentHandler) GetById() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handler.http.GeById"
		defer r.Body.Close()

		log := ch.logger.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		id := chi.URLParam(r, "id")
		if err := utils.IsValidObjectId(id); err != nil {
			utils.HandleError(log, w, r, "provided id is not correct", err,
				http.StatusBadRequest, "id", "provided id is not correct")
			return
		}

		comment, err := ch.svc.GetById(context.TODO(), id)
		if err != nil {
			utils.HandleError(log, w, r, "failed to get comment", err,
				http.StatusInternalServerError, "comment", "Failed to get comment")
			return
		}

		if comment == nil {
			w.WriteHeader(http.StatusNotFound)

			render.JSON(w, r, map[string]interface{}{})
			return
		}

		log.Info("comment successfully retrieved")
		render.JSON(w, r, comment)
	}
}
