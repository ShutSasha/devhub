package repository

import (
	"context"
	"fmt"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/models"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func (cr *CommentRepository) Create(ctx context.Context, comment *models.Comment) (primitive.ObjectID, error) {
	const op = "storage.mongodb.repository.Create"

	collection := cr.storage.Database("DevHubDB").Collection("comments")

	insertResult, err := collection.InsertOne(ctx, comment)
	if err != nil {
		return primitive.NilObjectID, fmt.Errorf("%s: %w", op, err)
	}

	oid := insertResult.InsertedID.(primitive.ObjectID)

	return oid, nil
}
