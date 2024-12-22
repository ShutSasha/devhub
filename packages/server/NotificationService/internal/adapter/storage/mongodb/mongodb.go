package mongodb

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/mongo"
	"go.mongodb.org/mongo-driver/mongo/options"
)

type Storage struct {
	*mongo.Client
}

func New(storagePath string) (*Storage, error) {
	const op = "internal.adapter.storage.mongodb.New"

	db, err := mongo.Connect(
		context.Background(),
		options.Client().ApplyURI(storagePath),
	)
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	return &Storage{db}, nil
}

func (s *Storage) Stop() error {
	return s.Disconnect(context.TODO())
}
