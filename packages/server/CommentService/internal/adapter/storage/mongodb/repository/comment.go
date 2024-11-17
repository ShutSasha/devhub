package repository

import (
	"context"
	"fmt"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/storage/mongodb"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/dtos"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/models"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
)

type CommentRepository struct {
	storage *mongodb.Storage
}

func NewCommentRepository(storage *mongodb.Storage) *CommentRepository {
	return &CommentRepository{
		storage,
	}
}

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

	post := &dtos.CommentDto{}
	if cursor.Next(ctx) {
		if err := cursor.Decode(post); err != nil {
			return nil, fmt.Errorf("%s: %w", op, err)
		}
	}

	if err := cursor.Err(); err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	return post, nil
}

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
