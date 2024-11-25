package repository

import (
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/storage/mongodb"
)

type CommentRepository struct {
	storage *mongodb.Storage
}

func NewCommentRepository(storage *mongodb.Storage) *CommentRepository {
	return &CommentRepository{
		storage,
	}
}
