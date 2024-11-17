package mongodb

import (
	"context"
	"fmt"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/interfaces"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
)

func (s *Storage) GetById(
	ctx context.Context,
	postId primitive.ObjectID,
	fileProvider interfaces.FileProvider,
) (*storage.PostModel, error) {
	const op = "storage.mongodb.GetById"

	collection := s.db.Database("DevHubDB").Collection("posts")

	pipeline := mongo.Pipeline{
		bson.D{
			{Key: "$match", Value: bson.D{
				{Key: "_id", Value: postId},
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
		bson.D{
			{Key: "$lookup", Value: bson.D{
				{Key: "from", Value: "comments"},
				{Key: "localField", Value: "comments"},
				{Key: "foreignField", Value: "_id"},
				{Key: "as", Value: "comments"},
			}},
		},
		bson.D{
			{Key: "$lookup", Value: bson.D{
				{Key: "from", Value: "users"},
				{Key: "localField", Value: "comments.user"},
				{Key: "foreignField", Value: "_id"},
				{Key: "as", Value: "commentAuthor"},
			}},
		},
		bson.D{
			{Key: "$unwind", Value: bson.D{
				{Key: "path", Value: "$commentAuthor"},
				{Key: "preserveNullAndEmptyArrays", Value: true},
			}},
		},
		bson.D{
			{Key: "$set", Value: bson.D{
				{Key: "comments", Value: bson.D{
					{Key: "$map", Value: bson.D{
						{Key: "input", Value: "$comments"},
						{Key: "as", Value: "comment"},
						{Key: "in", Value: bson.D{
							{Key: "_id", Value: "$$comment._id"},
							{Key: "user", Value: "$commentAuthor"},
							{Key: "post", Value: "$$comment.post"},
							{Key: "commentText", Value: "$$comment.commentText"},
							{Key: "createdAt", Value: "$$comment.createdAt"},
						}},
					}},
				}},
			}},
		},
	}

	cursor, err := collection.Aggregate(ctx, pipeline)
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}
	defer cursor.Close(ctx)

	post := &storage.PostModel{}
	if cursor.Next(ctx) {
		if err := cursor.Decode(post); err != nil {
			return nil, fmt.Errorf("%s: %w", op, err)
		}
	}

	return post, nil
}
