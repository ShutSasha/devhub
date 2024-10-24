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
		HeaderImage: "",
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
		if errors.Is(err, mongo.ErrNoDocuments) {
			return nil, storage.ErrPostNotFound
		}

		return nil, fmt.Errorf("%s: %w", op, err)
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
	description string,
	headerImage string,
	tags []string,
) error {
	const op = "storage.mongodb.Update"

	// TODO: add validation

	collection := s.db.Database("DevHubDB").Collection("posts")

	filter := bson.M{"_id": postId}

	update := bson.M{"$set": bson.M{
		"title":        title,
		"description":  description,
		"header_image": headerImage,
		"tags":         tags,
	}}

	_, err := collection.UpdateOne(ctx, filter, update)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}
