package mongodb

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

func (s *Storage) Update(
	ctx context.Context,
	postId primitive.ObjectID,
	title string,
	content string,
	headerImage string,
	tags []string,
) error {
	const op = "storage.mongodb.Update"

	if len(tags) < 1 {
		tags = []string{}
	}

	collection := s.db.Database("DevHubDB").Collection("posts")

	filter := bson.M{"_id": postId}

	updateFields := bson.M{}
	if title != "" {
		updateFields["title"] = title
	}

	if content != "" {
		updateFields["content"] = content
	}

	if headerImage != "" {
		updateFields["headerImage"] = headerImage
	}

	if tags != nil {
		updateFields["tags"] = tags
	}

	update := bson.M{}
	if len(updateFields) > 0 {
		update["$set"] = updateFields
	}

	if len(update) == 0 {
		return nil
	}

	_, err := collection.UpdateOne(ctx, filter, update)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}
