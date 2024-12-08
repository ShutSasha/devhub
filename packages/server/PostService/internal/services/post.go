package services

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type PostService struct {
	commentToPostAdder     CommentToPostAdder
	commentFromPostRemover CommentFromPostRemover
	userSavedUpdater       UserSavedUpdater
}

func New(
	commentToPostAdder CommentToPostAdder,
	commentFromPostRemover CommentFromPostRemover,
	userSavedUpdater UserSavedUpdater,
) *PostService {
	return &PostService{
		commentToPostAdder,
		commentFromPostRemover,
		userSavedUpdater,
	}
}

type CommentToPostAdder interface {
	AddCommentID(
		ctx context.Context,
		postId primitive.ObjectID,
		commentId primitive.ObjectID,
	) error
}

type CommentFromPostRemover interface {
	RemoveCommentFromPost(
		ctx context.Context,
		postId primitive.ObjectID,
		commentId primitive.ObjectID,
	) error
}

type UserSavedUpdater interface {
	UserSavedPost(
		ctx context.Context,
		postId primitive.ObjectID,
		value int,
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

func (ps *PostService) RemoveCommentFromPost(
	ctx context.Context,
	postId string,
	commentId string,
) error {
	const op = "services.RemoveCommentFromPost"

	objectPostId, err := primitive.ObjectIDFromHex(postId)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	objectCommentId, err := primitive.ObjectIDFromHex(commentId)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	if err := ps.commentFromPostRemover.RemoveCommentFromPost(ctx, objectPostId, objectCommentId); err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}

func (ps *PostService) UpdateSavedPost(
	ctx context.Context,
	postId string,
	value int,
) error {
	const op = "services.UpdateSavedPost"

	objectPostId, err := primitive.ObjectIDFromHex(postId)
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	if err := ps.userSavedUpdater.UserSavedPost(ctx, objectPostId, value); err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}
