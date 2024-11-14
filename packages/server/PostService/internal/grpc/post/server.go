package postgrpc

import (
	"context"
	"fmt"

	postv1 "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/gen/go/post"
	"google.golang.org/grpc"
)

type PostService interface {
	AddCommentToUser(ctx context.Context, commentId string, postId string) error
}

type postAPI struct {
	postv1.UnimplementedPostServiceServer
	postService PostService
}

func Register(gRPCServer *grpc.Server, postService PostService) {
	postv1.RegisterPostServiceServer(gRPCServer, &postAPI{postService: postService})
}

func (p *postAPI) AddCommentToUser(
	ctx context.Context,
	in *postv1.AddCommentRequest,
) (*postv1.AddCommentResponse, error) {
	const op = "grpc.post.AddCommentToUser"

	err := p.postService.AddCommentToUser(context.TODO(), in.CommentId, in.PostId)
	if err != nil {
		return &postv1.AddCommentResponse{
			Success: false,
			Message: err.Error(),
		}, fmt.Errorf("%s: %w", op, err)
	}

	return &postv1.AddCommentResponse{
		Success: true,
		Message: "",
	}, nil
}
