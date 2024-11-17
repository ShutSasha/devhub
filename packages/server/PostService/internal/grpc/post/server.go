package postgrpc

import (
	"context"
	"fmt"

	postv1 "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/gen/go/post"
	"google.golang.org/grpc"
)

type PostService interface {
	AddCommentToPost(ctx context.Context, commentId string, postId string) error
	RemoveCommentFromPost(ctx context.Context, postId string, commentId string) error
}

type postAPI struct {
	postv1.UnimplementedPostServiceServer
	postService PostService
}

func Register(gRPCServer *grpc.Server, postService PostService) {
	postv1.RegisterPostServiceServer(gRPCServer, &postAPI{postService: postService})
}

func (p *postAPI) AddCommentToPost(
	ctx context.Context,
	in *postv1.AddCommentRequest,
) (*postv1.AddCommentToPostResponse, error) {
	const op = "grpc.post.AddCommentToUser"

	err := p.postService.AddCommentToPost(context.TODO(), in.CommentId, in.PostId)
	if err != nil {
		return &postv1.AddCommentToPostResponse{
			Success: false,
			Message: err.Error(),
		}, fmt.Errorf("%s: %w", op, err)
	}

	return &postv1.AddCommentToPostResponse{
		Success: true,
		Message: "",
	}, nil
}

func (p *postAPI) RemoveCommentFromPost(ctx context.Context, in *postv1.RemoveCommentRequest) (*postv1.RemoveCommentResponse, error) {
	const op = "grpc.post.RemoveCommentFromPost"

	err := p.postService.RemoveCommentFromPost(context.TODO(), in.PostId, in.CommentId)
	if err != nil {
		return &postv1.RemoveCommentResponse{
			Success: false,
			Message: err.Error(),
		}, fmt.Errorf("%s: %w", op, err)
	}

	return &postv1.RemoveCommentResponse{
		Success: true,
		Message: "",
	}, nil
}
