package interfaces

import (
	"context"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage"
)

type PostSearcher interface {
	Search(
		ctx context.Context,
		sortBy int,
		query string,
		tags []string,
		fileProvider FileProvider,
		page int,
		limit int,
	) ([]storage.PostModel, int, error)
}
