package port

import (
	"context"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/dtos"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/domain/models"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type CommentService interface {
	Create(ctx context.Context, userId string, postId string, content string) (primitive.ObjectID, error)
	GetById(ctx context.Context, id string) (*dtos.CommentDto, error)
}

type CommentRepository interface {
	Create(ctx context.Context, comment *models.Comment) (primitive.ObjectID, error)
	GetById(ctx context.Context, id primitive.ObjectID) (*dtos.CommentDto, error)
}
