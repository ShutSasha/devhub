package mongodb

import (
	"context"
	"errors"
	"fmt"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/interfaces"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
)

func (s *Storage) GetPaginated(ctx context.Context, limit, page int, fileProvider interfaces.FileProvider) ([]storage.PostModel, error) {
	const op = "storage.mongodb.GetPaginated"

	collection := s.db.Database("DevHubDB").Collection("posts")
	pipeline := mongo.Pipeline{
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
				{Key: "as", Value: "commentAuthors"},
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
							{Key: "user", Value: bson.D{
								{Key: "$arrayElemAt", Value: bson.A{
									bson.D{{Key: "$filter", Value: bson.D{
										{Key: "input", Value: "$commentAuthors"},
										{Key: "as", Value: "author"},
										{Key: "cond", Value: bson.D{
											{Key: "$eq", Value: bson.A{"$$author._id", "$$comment.user"}},
										}},
									}}},
									0,
								}},
							}},
							{Key: "post", Value: "$$comment.post"},
							{Key: "commentText", Value: "$$comment.commentText"},
							{Key: "createdAt", Value: "$$comment.createdAt"},
						}},
					}},
				}},
			}},
		},
		bson.D{
			{Key: "$sort", Value: bson.D{{Key: "createdAt", Value: -1}}},
		},
		bson.D{
			{Key: "$skip", Value: int64((page - 1) * limit)},
		},
		bson.D{
			{Key: "$limit", Value: int64(limit)},
		},
	}

	cursor, err := collection.Aggregate(ctx, pipeline)
	if err != nil {
		if errors.Is(err, mongo.ErrNilCursor) {
			return nil, storage.ErrPostsNotFound
		}
		return nil, fmt.Errorf("%s: could not retrieve paginated posts: %w", op, err)
	}
	defer cursor.Close(ctx)

	var posts []storage.PostModel
	if err := cursor.All(ctx, &posts); err != nil {
		return nil, fmt.Errorf("%s: could not decode results: %w", op, err)
	}

	return posts, nil
}
