package app

import (
	"log/slog"
	"time"

	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/adapter/storage/mongodb"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/adapter/storage/mongodb/repository"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/app/grpc"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/app/http"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/service"
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
	notificationServicePort int,
) *App {
	storage, err := mongodb.New(storagePath)
	if err != nil {
		panic(err)
	}

	repos := repository.NewNotificationRepository(storage)
	service := service.NewNotificationService(repos)

	httpApp := http.New(
		log,
		service,
		httpPort,
		timeout,
	)

	grpcApp := grpc.New(
		log,
		service,
		notificationServicePort,
	)

	return &App{
		HttpApp: httpApp,
		GrpcApp: grpcApp,
	}
}
