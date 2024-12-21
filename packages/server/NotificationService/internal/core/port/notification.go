package port

import (
	"context"

	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/domain/dtos"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/domain/models"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type NotificationService interface {
	Create(ctx context.Context, notification models.Notification) (primitive.ObjectID, error)
	GetNotifications(ctx context.Context, userId primitive.ObjectID, limit, page int) ([]dtos.NotificationDto, error)
	GetByID(ctx context.Context, id primitive.ObjectID) (dtos.NotificationDto, error)
}

type NotificationRepository interface {
	Create(ctx context.Context, notification models.Notification) (primitive.ObjectID, error)
	GetNotifications(ctx context.Context, userId primitive.ObjectID, limit, page int) ([]dtos.NotificationDto, error)
	GetByID(ctx context.Context, id primitive.ObjectID) (dtos.NotificationDto, error)
}
