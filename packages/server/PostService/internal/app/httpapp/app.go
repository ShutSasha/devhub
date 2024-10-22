package httpapp

import (
	"errors"
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/go-chi/chi/v5"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/delete"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/get"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/save"
)

type App struct {
	Server *http.Server
}

func New(
	postSaver save.PostSaver,
	postProvider get.PostProvider,
	postRemover delete.PostRemover,
	port int,
	timout time.Duration,
) *App {

	router := chi.NewRouter()

	router.Route("/api/post", func(r chi.Router) {
		r.Post("/create", save.New(postSaver))

		r.Get("/{id}", get.New(postProvider))

		r.Delete("/{id}", delete.New(postRemover))
	})

	httpServer := &http.Server{
		Addr:         ":" + strconv.Itoa(port),
		Handler:      router,
		ReadTimeout:  timout,
		WriteTimeout: timout,
	}

	return &App{
		Server: httpServer,
	}
}

func (a *App) Run() error {
	const op = "httpapp.Run"

	if err := a.Server.ListenAndServe(); !errors.Is(err, http.ErrServerClosed) {
		return fmt.Errorf("%s: %w", op, err)
	}
	return nil
}
