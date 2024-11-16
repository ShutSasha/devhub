package services

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type PostService struct {
	commentToPostAdder CommentToPostAdder
}

func New(commentToPostAdder CommentToPostAdder) *PostService {
	return &PostService{commentToPostAdder}
}

type CommentToPostAdder interface {
	AddCommentID(
		ctx context.Context,
		postId primitive.ObjectID,
		commentId primitive.ObjectID,
	) error
}

func (ps *PostService) AddCommentToPost(ctx context.Context, commentId string, postId string) error {
	const op = "services.AddCommentToPost"

	objectPostId, err := primitive.ObjectIDFromHex(postId)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	objectCommentId, err := primitive.ObjectIDFromHex(commentId)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	if err = ps.commentToPostAdder.AddCommentID(context.TODO(), objectPostId, objectCommentId); err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil

}
