package main

import (
	"context"
	"log"
	"os"
	"os/signal"
	"syscall"
	"time"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/app"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/config"
)

func main() {
	cfg := config.MustLoad()

	application := app.New(cfg.StoragePath, cfg.Http.Port, cfg.Http.Timeout)

	go func() {
		sigChan := make(chan os.Signal, 1)
		signal.Notify(sigChan, syscall.SIGINT, syscall.SIGTERM)
		<-sigChan

		shutdownCtx, shutdownRelease := context.WithTimeout(context.Background(), 10*time.Second)
		defer shutdownRelease()

		if err := application.HttpServer.Server.Shutdown(shutdownCtx); err != nil {
			log.Fatalf("HTTP shutdown error: %v", err)
		}
	}()

	if err := application.HttpServer.Run(); err != nil {
		log.Fatalf("HTTP server error: %v", err)
	}
}
