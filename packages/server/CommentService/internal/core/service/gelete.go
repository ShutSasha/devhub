package service

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

func (cs *CommentService) Delete(ctx context.Context, id string) error {
	const op = "core.service.Delete"

	oid, err := primitive.ObjectIDFromHex(id)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	cs.repo.Delete(ctx, oid)
	if err = cs.repo.Delete(ctx, oid); err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}
