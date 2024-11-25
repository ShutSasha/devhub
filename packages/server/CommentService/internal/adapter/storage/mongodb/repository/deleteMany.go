package repository

import (
	"context"

	"go.mongodb.org/mongo-driver/bson"
)

func (cr *CommentRepository) DeleteMany(ctx context.Context, filter bson.D) error {
	const op = "mongodb.repository.DeleteMany"

	collection := cr.storage.Database("DevHubDB").Collection("comments")

	_, err := collection.DeleteMany(ctx, filter)

	if err != nil {
		return err
	}

	return nil
}
