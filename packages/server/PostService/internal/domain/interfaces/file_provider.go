package interfaces

import "context"

type FileProvider interface {
	Get(ctx context.Context, key string) ([]byte, error)
}
