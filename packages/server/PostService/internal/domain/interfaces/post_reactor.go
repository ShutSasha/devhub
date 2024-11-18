package interfaces

import (
	"context"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type PostReactor interface {
	React(ctx context.Context, postId primitive.ObjectID, likes, dislikes int) error
}
