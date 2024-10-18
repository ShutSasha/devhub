package app

import "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage/mongodb"

type App struct{}

func New(storagePath string) *App {
	_, err := mongodb.New(storagePath)
	if err != nil {
		panic(err)
	}

	return &App{}
}
