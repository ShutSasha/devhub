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
		Comments:    []primitive.ObjectID{},
		Tags:        tags,
	}

	insertResult, err := collection.InsertOne(ctx, post)
	if err != nil {
		return primitive.NilObjectID, fmt.Errorf("%s: %w", op, err)
	}

	oid := insertResult.InsertedID.(primitive.ObjectID)

	return oid, nil
}

func (s *Storage) Remove(
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

func (s *Storage) RemoveCommentFromPost(
	ctx context.Context,
	postId primitive.ObjectID,
	commentId primitive.ObjectID,
) error {
	const op = "storage.mongodb.RemoveCommentFromPost"

	collection := s.db.Database("DevHubDB").Collection("posts")

	filter := bson.M{"_id": postId}

	update := bson.M{"$pull": bson.M{"comments": commentId}}

	_, err := collection.UpdateOne(ctx, filter, update)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}
