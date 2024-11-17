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
	// ToggleReaction(
	// 	ctx context.Context,
	// 	postId primitive.ObjectID,
	// 	userId string,
	// 	action string, // "like" или "dislike"
	// ) error
}
