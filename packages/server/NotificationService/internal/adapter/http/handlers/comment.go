package handlers

import (
	"log/slog"
	"net/http"
	"strconv"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/dtos"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/port"
	dtoMapper "github.com/dranikpg/dto-mapper"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
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
// @Param limit query int false "limit"
// @Param page query int false "page"
func (h *NotificationHandler) GetNotifications(w http.ResponseWriter, r *http.Request) {
	const op = "NotificationHandler.GetNotifications"

	log := h.logger.With(
		slog.String("operation", op),
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

	notifications, err := h.svc.GetNotifications(r.Context(), limit, page)
	if err != nil {
		log.Error("failed to get notifications", slog.Any("err", err.Error()))
		render.JSON(w, r, map[string]string{"error": "failed to get notifications"})
		return
	}

	var dtoNotifications []dtos.NotificationDtO
	if err := dtoMapper.Map(notifications, &dtoNotifications); err != nil {
		log.Error("failed to map notifications", slog.Any("err", err.Error()))
		render.JSON(w, r, map[string]string{"error": "failed to get notifications"})
		return
	}

	render.JSON(w, r, dtoNotifications)
}
