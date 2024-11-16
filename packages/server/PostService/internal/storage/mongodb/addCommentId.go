package mongodb

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func (s *Storage) AddCommentID(
	ctx context.Context,
	postId primitive.ObjectID,
	commentId primitive.ObjectID,
) error {
	const op = "storage.mongodb.AddCommentID"

	collection := s.db.Database("DevHubDB").Collection("posts")

	filter := bson.M{"_id": postId}

	update := bson.M{
		"$push": bson.M{"comments": commentId},
	}

	result, err := collection.UpdateOne(ctx, filter, update)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	if result.MatchedCount == 0 {
		return fmt.Errorf("%s: no post found with the given ID", op)
	}

	return nil
}
