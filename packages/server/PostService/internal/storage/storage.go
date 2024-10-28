package storage

import "errors"

var (
	ErrPostNotFound  = errors.New("post not found")
	ErrPostsNotFound = errors.New("posts not found")
)
