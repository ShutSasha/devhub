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

func (s *Storage) Search(ctx context.Context, sortBy string, query string,
	tags []string, fileProvider interfaces.FileProvider) ([]storage.PostModel, error) {
	const op = "storage.mongodb.Search"

	collection := s.db.Database("DevHubDB").Collection("posts")

	pipeline := mongo.Pipeline{}

	searchFilter := bson.D{}
	if query != "" {
		searchFilter = append(searchFilter, bson.E{
			Key: "$or", Value: bson.A{
				bson.M{"title": bson.M{"$regex": query, "$options": "i"}},
				bson.M{"content": bson.M{"$regex": query, "$options": "i"}},
			},
		})
	}

	if len(tags) > 0 {
		var regexTagFilters []bson.M
		for _, tag := range tags {
			regexTagFilters = append(regexTagFilters, bson.M{
				"tags": bson.M{
					"$elemMatch": bson.M{
						"$regex":   tag,
						"$options": "i",
					},
				},
			})
		}

		searchFilter = append(searchFilter, bson.E{Key: "$and", Value: regexTagFilters})
	}

	if len(searchFilter) > 0 {
		pipeline = append(pipeline, bson.D{{Key: "$match", Value: searchFilter}})
	}

	// agregate user

	pipeline = append(pipeline, bson.D{
		{Key: "$lookup", Value: bson.D{
			{Key: "from", Value: "users"},
			{Key: "localField", Value: "user"},
			{Key: "foreignField", Value: "_id"},
			{Key: "as", Value: "user"},
		}},
	})

	pipeline = append(pipeline, bson.D{
		{Key: "$unwind", Value: bson.D{
			{Key: "path", Value: "$user"},
			{Key: "preserveNullAndEmptyArrays", Value: true},
		}},
	})

	// agregate comments

	pipeline = append(pipeline, bson.D{
		{Key: "$lookup", Value: bson.D{
			{Key: "from", Value: "comments"},
			{Key: "localField", Value: "comments"},
			{Key: "foreignField", Value: "_id"},
			{Key: "as", Value: "comments"},
		}},
	})

	// agregate comments.user

	pipeline = append(pipeline, bson.D{
		{Key: "$lookup", Value: bson.D{
			{Key: "from", Value: "users"},
			{Key: "localField", Value: "comments.user"},
			{Key: "foreignField", Value: "_id"},
			{Key: "as", Value: "commentAuthor"},
		}},
	})

	// Unwind commentAuthor for each comment
	pipeline = append(pipeline, bson.D{
		{Key: "$unwind", Value: bson.D{
			{Key: "path", Value: "$commentAuthor"},
			{Key: "preserveNullAndEmptyArrays", Value: true},
		}},
	})

	pipeline = append(pipeline, bson.D{
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
	})

	var sortField string
	switch sortBy {
	case "date":
		sortField = "created_at"
	case "likes":
		sortField = "likes"
	default:
		sortField = "likes"
	}

	pipeline = append(pipeline, bson.D{{Key: "$sort", Value: bson.D{{Key: sortField, Value: -1}}}})

	cursor, err := collection.Aggregate(ctx, pipeline)
	if err != nil {
		if errors.Is(err, mongo.ErrNilCursor) {
			return nil, storage.ErrPostsNotFound
		}
		return nil, fmt.Errorf("%s: could not execute search query: %w", op, err)
	}
	defer cursor.Close(ctx)

	var posts []storage.PostModel
	if err := cursor.All(ctx, &posts); err != nil {
		return nil, fmt.Errorf("%s: could not decode results: %w", op, err)
	}

	return posts, nil
}
