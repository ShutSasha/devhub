package mongodb

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func (s *Storage) React(
	ctx context.Context,
	postId primitive.ObjectID,
	likes, dislikes int,
) error {
	const op = "storage.mongodb.React"

	collection := s.db.Database("DevHubDB").Collection("posts")

	filter := bson.M{"_id": postId}
	update := bson.M{
		"$inc": bson.M{
			"likes":    likes,
			"dislikes": dislikes,
		},
	}

	_, err := collection.UpdateOne(ctx, filter, update)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}

func (s *Storage) UserSavedPost(
	ctx context.Context,
	postId primitive.ObjectID,
	value int,
) error {
	const op = "storage.mongodb.UserSaved"

	collection := s.db.Database("DevHubDB").Collection("posts")

	filter := bson.M{"_id": postId}
	update := bson.M{
		"$inc": bson.M{
			"saved": value,
		},
	}

	_, err := collection.UpdateOne(ctx, filter, update)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}
