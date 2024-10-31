package get

import (
	"context"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type PostProvider interface {
	GetById(
		ctx context.Context,
		postId primitive.ObjectID,
	) (*storage.PostModel, error)

	GetPaginated(
		ctx context.Context,
		limit int,
		page int,
	) ([]storage.PostModel, error)
}
