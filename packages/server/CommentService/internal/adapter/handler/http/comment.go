package http

import (
	"log/slog"

	pb "github.com/ShutSasha/devhub/packages/server/CommentService/gen/go/post"
	ub "github.com/ShutSasha/devhub/packages/server/CommentService/gen/go/user"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/port"
)

type CommentHandler struct {
	logger         *slog.Logger
	svc            port.CommentService
	grpcPostClient pb.PostServiceClient
	grpcUserClient ub.UserServiceClient
}

func NewCommentHandler(
	svc port.CommentService,
	log *slog.Logger,
	grpcPostClient pb.PostServiceClient,
	grpcUserClient ub.UserServiceClient,
) *CommentHandler {
	return &CommentHandler{
		log,
		svc,
		grpcPostClient,
		grpcUserClient,
	}
}
