package interfaces

import (
	"context"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type PostReactor interface {
	AddLike(ctx context.Context, postId primitive.ObjectID) error
	RemoveLike(ctx context.Context, postId primitive.ObjectID) error
	AddDislike(ctx context.Context, postId primitive.ObjectID) error
	RemoveDislike(ctx context.Context, postId primitive.ObjectID) error
}
