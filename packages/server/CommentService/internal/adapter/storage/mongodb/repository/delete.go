package repository

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func (cr *CommentRepository) Delete(ctx context.Context, id primitive.ObjectID) error {
	const op = "mongodb.repository.Delete"

	collection := cr.storage.Database("DevHubDB").Collection("comments")

	filter := bson.M{"_id": id}

	_, err := collection.DeleteOne(context.TODO(), filter)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}
