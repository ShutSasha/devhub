package service

import (
	"context"
	"fmt"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/dtos"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

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
