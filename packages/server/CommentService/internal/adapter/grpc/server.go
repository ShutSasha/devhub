package grpc

import (
	"context"

	commentgen "github.com/ShutSasha/devhub/packages/server/CommentService/gen/go/comment"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/port"
	"google.golang.org/grpc"
)

type CommentsRemover interface {
	DeleteByPredicat(ctx context.Context, pred port.Predicate) error
}

type serverAPI struct {
	commentgen.UnimplementedCommentServiceServer
	commentsRemover CommentsRemover
}

func Register(gRPCServer *grpc.Server, commentsRemover CommentsRemover) {
	commentgen.RegisterCommentServiceServer(
		gRPCServer,
		&serverAPI{commentsRemover: commentsRemover},
	)
}

func (s *serverAPI) RemoveComments(
	ctx context.Context,
	in *commentgen.RemoveCommentRequest,
) (*commentgen.RemoveCommentResponse, error) {
	const op = "internal.adapter.grpc.RemoveComments"

	if err := s.commentsRemover.DeleteByPredicat(ctx, port.Predicate{
		Collection: in.From,
		Id:         in.Id,
	}); err != nil {
		return &commentgen.RemoveCommentResponse{
			Success: false,
			Message: err.Error(),
		}, err
	}

	return &commentgen.RemoveCommentResponse{
		Success: true,
		Message: "",
	}, nil
}
