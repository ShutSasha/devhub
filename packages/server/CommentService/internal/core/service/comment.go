package service

import (
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/port"
)

type CommentService struct {
	repo port.CommentRepository
}

func NewCommentService(repo port.CommentRepository) *CommentService {
	return &CommentService{repo}
}
