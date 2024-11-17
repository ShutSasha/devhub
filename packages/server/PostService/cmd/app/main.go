package main

import (
	"context"
	"log/slog"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/app"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/config"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/handlers/slogpretty"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/sl"
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
		cfg.Grpc.UserServicePort,
		cfg.StoragePath,
		log,
		cfg.Aws.Region,
		cfg.Aws.AccessKey,
		cfg.Aws.SecretKey,
		cfg.Aws.Bucket,
		cfg.Http.Port,
		cfg.Http.Timeout,
		cfg.Grpc.PostServicePort,
	)
	done := make(chan os.Signal, 1)
	signal.Notify(done, os.Interrupt, syscall.SIGINT, syscall.SIGTERM)

	go func() {
		if err := application.HttpApp.Run(); err != nil {
			log.Error("postService: failed to start http server", sl.Err(err))
		}
	}()

	log.Info("postService: http server started")

	go func() {
		if err := application.GRPCApp.Run(); err != nil {
			log.Error("postService: failed to start grpc server", sl.Err(err))
		}
	}()

	log.Info("postService: grpc server started")

	<-done
	log.Info("postService: stopping server")

	ctx, cancel := context.WithTimeout(context.Background(), 10*time.Second)
	defer cancel()

	if err := application.HttpApp.Server.Shutdown(ctx); err != nil {
		log.Error("postService: failed to stop server", sl.Err(err))

		return
	}

	log.Info("postService: server stopped")
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
