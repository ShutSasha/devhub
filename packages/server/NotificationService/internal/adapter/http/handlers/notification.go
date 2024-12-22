package handlers

import (
	"log/slog"
	"net/http"

	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/domain/dtos"
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

	unreadNotifications, err := h.svc.GetNotifications(r.Context(), userObjectId, false)
	if err != nil {
		log.Error("failed to get notifications", slog.Any("err", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		render.JSON(w, r, map[string]string{"error": "failed to get notifications"})
		return
	}

	if len(unreadNotifications) == 0 {
		unreadNotifications = []dtos.NotificationDto{}
	}

	readNotifications, err := h.svc.GetNotifications(r.Context(), userObjectId, true)
	if err != nil {
		log.Error("failed to get notifications", slog.Any("err", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		render.JSON(w, r, map[string]string{"error": "failed to get notifications"})
		return
	}

	if len(readNotifications) == 0 {
		readNotifications = []dtos.NotificationDto{}
	}

	render.JSON(w, r, map[string]interface{}{
		"unread": unreadNotifications,
		"read":   readNotifications,
	})
}

// ReadNotification godoc
// @Summary Read notification
// @Description Read notification
// @Tags notifications
// @Accept json
// @Produce json
// @Param notification_id path string true "notification_id"
// @Router /api/notifications/{notification_id} [patch]
func (h *NotificationHandler) ReadNotification(w http.ResponseWriter, r *http.Request) {
	const op = "NotificationHandler.ReadNotification"

	log := h.logger.With(
		slog.String("operation", op),
		slog.String("request_id", middleware.GetReqID(r.Context())),
	)

	notificationId := chi.URLParam(r, "notification_id")
	if notificationId == "" {
		log.Error("post_id is required")
		w.WriteHeader(http.StatusBadRequest)
		render.JSON(w, r, map[string]string{"error": "post_id is required"})
		return
	}

	notificationObjectId, err := primitive.ObjectIDFromHex(notificationId)
	if err != nil {
		log.Error("invalid post_id", slog.Any("notification_id", notificationId))
		w.WriteHeader(http.StatusBadRequest)
		render.JSON(w, r, map[string]string{"error": "invalid notification id"})
		return
	}

	if err := h.svc.ReadNotification(r.Context(), notificationObjectId); err != nil {
		log.Error("failed to read notification", slog.Any("err", err.Error()))
		w.WriteHeader(http.StatusInternalServerError)
		render.JSON(w, r, map[string]string{"error": "failed to read notification"})
		return
	}

	render.JSON(w, r, map[string]string{"message": "notification read"})
}
