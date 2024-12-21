package service

import (
	"context"

	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/domain/dtos"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/domain/models"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/port"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type NotificationService struct {
	repo port.NotificationService
}

func NewNotificationService(repo port.NotificationService) *NotificationService {
	return &NotificationService{repo}
}

func (s *NotificationService) GetNotifications(ctx context.Context, userId primitive.ObjectID, limit, page int) ([]dtos.NotificationDto, error) {
	return s.repo.GetNotifications(ctx, userId, limit, page)
}

func (s *NotificationService) Create(ctx context.Context, model models.Notification) (primitive.ObjectID, error) {
	return s.repo.Create(ctx, model)
}

func (s *NotificationService) GetByID(ctx context.Context, id primitive.ObjectID) (dtos.NotificationDto, error) {
	return s.repo.GetByID(ctx, id)
}
