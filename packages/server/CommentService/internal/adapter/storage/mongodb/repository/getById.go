package repository

import (
	"context"
	"fmt"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/dtos"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
)

func (cr *CommentRepository) GetById(ctx context.Context, id primitive.ObjectID) (*dtos.CommentDto, error) {
	const op = "storage.mongodb.repository.GetById"

	collection := cr.storage.Database("DevHubDB").Collection("comments")

	pipeline := mongo.Pipeline{
		bson.D{
			{Key: "$match", Value: bson.D{
				{Key: "_id", Value: id},
			}},
		},
		bson.D{
			{Key: "$lookup", Value: bson.D{
				{Key: "from", Value: "users"},
				{Key: "localField", Value: "user"},
				{Key: "foreignField", Value: "_id"},
				{Key: "as", Value: "user"},
			}},
		},
		bson.D{
			{Key: "$unwind", Value: bson.D{
				{Key: "path", Value: "$user"},
				{Key: "preserveNullAndEmptyArrays", Value: true},
			}},
		},
	}

	cursor, err := collection.Aggregate(ctx, pipeline)
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}
	defer cursor.Close(ctx)

	if !cursor.Next(ctx) {
		return nil, nil
	}

	post := &dtos.CommentDto{}
	if err := cursor.Decode(post); err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	if err := cursor.Err(); err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	return post, nil
}
