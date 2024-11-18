package port

import (
	"context"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/dtos"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/models"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type Predicate struct {
	Collection string
	Id         string
}

type CommentService interface {
	Create(ctx context.Context, userId string, postId string, content string) (primitive.ObjectID, error)
	GetById(ctx context.Context, id string) (*dtos.CommentDto, error)
	Delete(ctx context.Context, id string) error
	DeleteByPredicat(ctx context.Context, pred Predicate) error
}

type CommentRepository interface {
	Create(ctx context.Context, comment *models.Comment) (primitive.ObjectID, error)
	GetById(ctx context.Context, id primitive.ObjectID) (*dtos.CommentDto, error)
	Delete(ctx context.Context, id primitive.ObjectID) error
	DeleteMany(ctx context.Context, filter bson.D) error
}
