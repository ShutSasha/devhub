package post

import (
	"context"
	"fmt"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type PostService struct {
	postSaver PostSaver
}

type PostSaver interface {
	SavePost(
		ctx context.Context,
		userId primitive.ObjectID,
		title string,
		description string,
		tags []string,
	) (primitive.ObjectID, error)
}

func New(
	postSaver PostSaver,
) *PostService {
	return &PostService{
		postSaver: postSaver,
	}
}

func (p *PostService) CreatePost(
	ctx context.Context,
	userId primitive.ObjectID,
	title string,
	description string,
	tags []string,
) (primitive.ObjectID, error) {
	const op = "service.post.CreatePost"

	uid, err := p.postSaver.SavePost(ctx, userId, title, description, tags)
	if err != nil {
		return primitive.NilObjectID, fmt.Errorf("%s: %w", op, err)
	}

	return uid, nil
}
