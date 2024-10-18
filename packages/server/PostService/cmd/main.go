package main

import (
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/app"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/config"
)

func main() {
	cfg := config.MustLoad()

	_ = app.New(cfg.StoragePath)
}
