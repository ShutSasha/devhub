package mongodb

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/mongo"
)

func (s *Storage) GetPopularTags(ctx context.Context, limit int) ([]string, error) {
	const op = "storage.mongodb.GetPopularTags"

	collection := s.db.Database("DevHubDB").Collection("posts")

	pipeline := mongo.Pipeline{

		{{Key: "$unwind", Value: "$tags"}},

		{{Key: "$group", Value: bson.D{
			{Key: "_id", Value: "$tags"},
			{Key: "count", Value: bson.D{{Key: "$sum", Value: 1}}},
		}}},

		{{Key: "$sort", Value: bson.D{{Key: "count", Value: -1}}}},

		{{Key: "$limit", Value: limit}},
	}

	cursor, err := collection.Aggregate(ctx, pipeline)
	if err != nil {
		return nil, fmt.Errorf("%s: could not execute aggregate query: %w", op, err)
	}
	defer cursor.Close(ctx)

	var results []struct {
		Tag   string `bson:"_id"`
		Count int    `bson:"count"`
	}
	if err := cursor.All(ctx, &results); err != nil {
		return nil, fmt.Errorf("%s: could not decode results: %w", op, err)
	}

	tags := make([]string, len(results))
	for i, result := range results {
		tags[i] = result.Tag
	}

	return tags, nil
}
