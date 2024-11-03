package interfaces

import "context"

type FileRemover interface {
	Remove(ctx context.Context, key string) error
}
