package interfaces

import "context"

type TagsProvider interface {
	GetPopularTags(ctx context.Context, limit int) ([]string, error)
}
