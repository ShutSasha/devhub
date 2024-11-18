package service

import (
	"context"
	"fmt"
	"time"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/models"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

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
