package interfaces

import (
	"context"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type PostUpdater interface {
	Update(
		ctx context.Context,
		postId primitive.ObjectID,
		title string,
		content string,
		headerImage string,
		tags []string,
	) error
}
