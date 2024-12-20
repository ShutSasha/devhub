package repository

import (
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/adapter/storage/mongodb"
)

type NotificationRepository struct {
	storage *mongodb.Storage
}

func NewNotificationRepository(storage *mongodb.Storage) *NotificationRepository {
	return &NotificationRepository{
		storage,
	}
}
