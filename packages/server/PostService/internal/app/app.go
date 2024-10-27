package app

import (
	"log/slog"
	"time"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/app/httpapp"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage/mongodb"
)

type App struct {
	HttpServer *httpapp.App
}

func New(
	log *slog.Logger,
	storagePath string,
	httpPort int,
	timeout time.Duration,
) *App {
	storage, err := mongodb.New(storagePath)
	if err != nil {
		panic(err)
	}

	httpApp := httpapp.New(
		log,
		storage,
		storage,
		storage,
		storage,
		httpPort,
		timeout,
	)

	return &App{
		HttpServer: httpApp,
	}
}
