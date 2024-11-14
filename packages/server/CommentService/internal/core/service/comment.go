package service

import (
	"context"
	"fmt"
	"time"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/dtos"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/models"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/port"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type CommentService struct {
	repo port.CommentRepository
}

func NewCommentService(repo port.CommentRepository) *CommentService {
	return &CommentService{repo}
}

func (cs *CommentService) Create(ctx context.Context, userId string, postId string, content string) (primitive.ObjectID, error) {
	const op = "core.service.Create"

	objectUserId, err := primitive.ObjectIDFromHex(userId)
	if err != nil {
		return primitive.NilObjectID, fmt.Errorf("%s: %w", op, err)
	}

	objectPostId, err := primitive.ObjectIDFromHex(postId)
	if err != nil {
		return primitive.NilObjectID, fmt.Errorf("%s: %w", op, err)
	}

	comment := &models.Comment{
		User:        objectUserId,
		Post:        objectPostId,
		CommentText: content,
		CreatedAt:   time.Now(),
	}

	commentId, err := cs.repo.Create(ctx, comment)
	if err != nil {
		return primitive.NilObjectID, fmt.Errorf("%s: %w", op, err)
	}

	return commentId, nil
}

func (cs *CommentService) GetById(ctx context.Context, id string) (*dtos.CommentDto, error) {
	const op = "core.service.GetById"

	oid, err := primitive.ObjectIDFromHex(id)
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	comment, err := cs.repo.GetById(ctx, oid)
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	return comment, nil
}
