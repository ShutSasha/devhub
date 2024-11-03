package interfaces

import (
	"context"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type PostRemover interface {
	Remove(
		ctx context.Context,
		postId primitive.ObjectID,
	) error
}
