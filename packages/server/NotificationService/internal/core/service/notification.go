package service

import (
	"context"

	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/domain/models"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/port"
)

type NotificationService struct {
	repo port.NotificationService
}

func NewNotificationService(repo port.NotificationService) *NotificationService {
	return &NotificationService{repo}
}

func (s *NotificationService) GetNotifications(ctx context.Context, limit, page int) ([]models.Notification, error) {
	return s.repo.GetNotifications(ctx, limit, page)
}
