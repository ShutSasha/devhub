package app

import (
	"log/slog"
	"time"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/storage/mongodb"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/storage/mongodb/repository"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/app/http"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/app/grpc"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/service"
)

type App struct {
	HttpApp *http.App
	GrpcApp *grpc.App
}

func New(
	log *slog.Logger,
	storagePath string,
	httpPort int,
	timeout time.Duration,
	postServicePort int,
	userServicePort int,
	commentServicePort int,
) *App {
	storage, err := mongodb.New(storagePath)
	if err != nil {
		panic(err)
	}

	repos := repository.NewCommentRepository(storage)
	service := service.NewCommentService(repos)

	httpApp := http.New(
		log,
		service,
		httpPort,
		timeout,
		postServicePort,
		userServicePort,
	)

	grpcApp := grpc.New(log, service, commentServicePort)

	return &App{
		HttpApp: httpApp,
		GrpcApp: grpcApp,
	}
}
