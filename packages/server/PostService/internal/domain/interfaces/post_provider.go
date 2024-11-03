package interfaces

import (
	"context"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type PostProvider interface {
	GetById(ctx context.Context, postId primitive.ObjectID, fileProvider FileProvider) (*storage.PostModel, error)
	GetPaginated(ctx context.Context, limit int, page int, fileProvider FileProvider) ([]storage.PostModel, error)
}
