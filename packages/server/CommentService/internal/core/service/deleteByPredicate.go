package service

import (
	"context"
	"fmt"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/port"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func (cs *CommentService) DeleteByPredicat(ctx context.Context, pred port.Predicate) error {
	const op = "core.service.DeleteByPredicat"

	oid, err := primitive.ObjectIDFromHex(pred.Id)
	if err != nil {
		return fmt.Errorf("failed to convert predicate id to oid: %w", err)
	}

	filter := bson.D{
		{Key: "post", Value: oid},
	}

	if err := cs.repo.DeleteMany(ctx, filter); err != nil {
		return fmt.Errorf("failed to delete comments: %w", err)
	}

	return nil
}
