package mongodb

import (
	"context"
	"fmt"
	"time"

	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/models"
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

func (s *Storage) SavePost(
	ctx context.Context,
	userId primitive.ObjectID,
	title string,
	description string,
	tags []string,
) (primitive.ObjectID, error) {
	const op = "storage.mongodb.SavePost"

	collection := s.db.Database("DevHubDB").Collection("posts")

	post := &models.Post{
		User:        userId,
		Title:       title,
		Description: description,
		CreatedAt:   time.Now(),
		Likes:       0,
		Dislikes:    0,
		Images:      []string{},
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

func (s *Storage) GetPostById(
	ctx context.Context,
	postId primitive.ObjectID,
) (*models.Post, error) {
	const op = "storage.mongodb.GetById"

	collection := s.db.Database("DevHubDB").Collection("posts")

	post := &models.Post{}
	filter := bson.M{"_id": postId}

	err := collection.FindOne(context.TODO(), filter).Decode(post)
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	return post, nil
}
