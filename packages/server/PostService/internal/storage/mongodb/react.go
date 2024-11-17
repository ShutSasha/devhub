package mongodb

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func (s *Storage) AddLike(ctx context.Context, postId primitive.ObjectID) error {
	const op = "storage.mongodb.AddLike"

	collection := s.db.Database("DevHubDB").Collection("posts")

	filter := bson.M{"_id": postId}
	update := bson.M{"$inc": bson.M{"likes": 1}}

	_, err := collection.UpdateOne(ctx, filter, update)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}

func (s *Storage) RemoveLike(ctx context.Context, postId primitive.ObjectID) error {
	const op = "storage.mongodb.RemoveLike"

	collection := s.db.Database("DevHubDB").Collection("posts")

	filter := bson.M{"_id": postId}
	update := bson.M{"$inc": bson.M{"likes": -1}}

	_, err := collection.UpdateOne(ctx, filter, update)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}

func (s *Storage) AddDislike(ctx context.Context, postId primitive.ObjectID) error {
	const op = "storage.mongodb.AddDislike"

	collection := s.db.Database("DevHubDB").Collection("posts")

	filter := bson.M{"_id": postId}
	update := bson.M{"$inc": bson.M{"dislikes": 1}}

	_, err := collection.UpdateOne(ctx, filter, update)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}

func (s *Storage) RemoveDislike(ctx context.Context, postId primitive.ObjectID) error {
	const op = "storage.mongodb.RemoveDislike"

	collection := s.db.Database("DevHubDB").Collection("posts")

	filter := bson.M{"_id": postId}
	update := bson.M{"$inc": bson.M{"dislikes": -1}}

	_, err := collection.UpdateOne(ctx, filter, update)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}
