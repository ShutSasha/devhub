package mongodb

import (
	"context"
	"errors"
	"fmt"
	"time"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/models"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage"
)

type Storage struct {
	db *mongo.Client
}

func New(storagePath string) (*Storage, error) {
	const op = "storage.mongodb.New"

	db, err := mongo.Connect(
		context.Background(),
		options.Client().ApplyURI(storagePath),
	)
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	return &Storage{db: db}, nil
}

func (s *Storage) Stop() error {
	return s.db.Disconnect(context.TODO())
}

func (s *Storage) Save(
	ctx context.Context,
	userId primitive.ObjectID,
	title string,
	content string,
	headerImage string,
	tags []string,
) (primitive.ObjectID, error) {
	const op = "storage.mongodb.Save"

	collection := s.db.Database("DevHubDB").Collection("posts")

	post := &models.Post{
		User:        userId,
		Title:       title,
		Content:     content,
		CreatedAt:   time.Now(),
		Likes:       0,
		Dislikes:    0,
		HeaderImage: headerImage,
		Comments:    []models.Comment{},
		Tags:        tags,
	}

	insertResult, err := collection.InsertOne(ctx, post)
	if err != nil {
		return primitive.NilObjectID, fmt.Errorf("%s: %w", op, err)
	}

	oid := insertResult.InsertedID.(primitive.ObjectID)

	return oid, nil
}

func (s *Storage) GetById(
	ctx context.Context,
	postId primitive.ObjectID,
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

func (s *Storage) Delete(
	ctx context.Context,
	postId primitive.ObjectID,
) error {
	const op = "storage.mongodb.Delete"

	collection := s.db.Database("DevHubDB").Collection("posts")

	filter := bson.M{"_id": postId}

	deleteResult, err := collection.DeleteOne(context.TODO(), filter)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}
	if deleteResult.DeletedCount < 1 {
		return fmt.Errorf("%s: %w", op, fmt.Errorf("no items deleted"))
	}

	return nil
}

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
		updateFields["header_image"] = headerImage
	}

	if tags != nil {
		updateFields["tags"] = tags
	}

	if len(updateFields) == 0 {
		return nil
	}

	update := bson.M{"$set": updateFields}

	_, err := collection.UpdateOne(ctx, filter, update)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}

func (s *Storage) Search(
	ctx context.Context,
	sortBy string,
	query string,
	tags []string,
) ([]storage.PostModel, error) {
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

func (s *Storage) GetPaginated(ctx context.Context, limit, page int) ([]storage.PostModel, error) {
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
			{Key: "$sort", Value: bson.D{{Key: "created_at", Value: -1}}},
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
