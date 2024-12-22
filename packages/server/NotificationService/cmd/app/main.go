package main

import (
	"context"
	"log/slog"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/adapter/config"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/app"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/lib/logger/handlers/slogpretty"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/lib/logger/sl"
)

const (
	envLocal = "local"
	envDev   = "dev"
	envProd  = "prod"
)

func main() {
	cfg := config.MustLoad()

	log := setupLogger(cfg.Env)

	application := app.New(
		log,
		cfg.StoragePath,
		cfg.Http.Port,
		cfg.Http.Timeout,
		cfg.Grpc.Port,
	)

	done := make(chan os.Signal, 1)
	signal.Notify(done, os.Interrupt, syscall.SIGINT, syscall.SIGTERM)

	go func() {
		if err := application.HttpApp.Run(); err != nil {
			log.Error("failed to start server", sl.Err(err))
		}
	}()

	log.Info("http server started")

	go func() {
		if err := application.GrpcApp.Run(); err != nil {
			log.Error("failed to start grpc server", sl.Err(err))
		}
	}()

	log.Info("grpc server started")

	<-done
	log.Info("stopping server")

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := application.HttpApp.Server.Shutdown(ctx); err != nil {
		log.Error("failed to stop http server", sl.Err(err))

		return
	}

	log.Info("server stopped")
}

func setupLogger(env string) *slog.Logger {
	var log *slog.Logger

	switch env {
	case envLocal:
		log = setupPrettySlog()
	case envDev:
		log = slog.New(
			slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{Level: slog.LevelDebug}),
		)
	case envProd:
		log = slog.New(
			slog.NewJSONHandler(os.Stdout, &slog.HandlerOptions{Level: slog.LevelInfo}),
		)
	}

	return log
}

func setupPrettySlog() *slog.Logger {
	opts := slogpretty.PrettyHandlerOptions{
		SlogOpts: &slog.HandlerOptions{
			Level: slog.LevelDebug,
		},
	}

	handler := opts.NewPrettyHandler(os.Stdout)

	return slog.New(handler)
}
