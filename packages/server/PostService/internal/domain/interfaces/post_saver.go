package interfaces

import (
	"context"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type PostSaver interface {
	Save(
		ctx context.Context,
		userId primitive.ObjectID,
		title string,
		content string,
		headerImage string,
		tags []string,
	) (primitive.ObjectID, error)
}
