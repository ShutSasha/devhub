package handlers

import (
	"log/slog"
	"net/http"
	"strconv"

	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/port"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type NotificationHandler struct {
	logger *slog.Logger
	svc    port.NotificationService
}

func NewNotificationHandler(
	svc port.NotificationService,
	log *slog.Logger,
) *NotificationHandler {
	return &NotificationHandler{
		log,
		svc,
	}
}

// GetNotifications godoc
// @Summary Get all notifications
// @Description Get all notifications
// @Tags notifications
// @Accept json
// @Produce json
// @Param user_id path string true "user_id"
// @Param limit query int false "limit"
// @Param page query int false "page"
// @Router       /api/notifications/{user_id} [get]
func (h *NotificationHandler) GetNotifications(w http.ResponseWriter, r *http.Request) {
	const op = "NotificationHandler.GetNotifications"

	log := h.logger.With(
		slog.String("operation", op),
		slog.String("request_id", middleware.GetReqID(r.Context())),
	)

	userId := chi.URLParam(r, "user_id")
	if userId == "" {
		log.Error("user_id is required")
		w.WriteHeader(http.StatusBadRequest)
		render.JSON(w, r, map[string]string{"error": "user_id is required"})
		return
	}

	userObjectId, err := primitive.ObjectIDFromHex(userId)
	if err != nil {
		log.Error("invalid user_id", slog.Any("user_id", userId))
		w.WriteHeader(http.StatusBadRequest)
		render.JSON(w, r, map[string]string{"error": "invalid user_id"})
		return
	}

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

	notifications, err := h.svc.GetNotifications(r.Context(), userObjectId, limit, page)
	if err != nil {
		log.Error("failed to get notifications", slog.Any("err", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		render.JSON(w, r, map[string]string{"error": "failed to get notifications"})
		return
	}

	if len(notifications) == 0 {
		render.JSON(w, r, map[interface{}]interface{}{})
	}

	render.JSON(w, r, notifications)
}
