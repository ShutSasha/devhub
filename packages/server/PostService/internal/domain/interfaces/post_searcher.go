package interfaces

import (
	"context"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage"
)

type PostSearcher interface {
	Search(
		ctx context.Context,
		sortBy string,
		query string,
		tags []string,
		fileProvider FileProvider,
	) ([]storage.PostModel, error)
}
