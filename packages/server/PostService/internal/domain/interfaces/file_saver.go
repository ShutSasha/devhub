package interfaces

import (
	"bytes"
	"context"
)

type FileSaver interface {
	Save(ctx context.Context, key string, buf bytes.Buffer) error
}
