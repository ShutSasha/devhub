package httpapp

import (
	"errors"
	"fmt"
	"net/http"
	"strconv"
	"time"

	"github.com/go-chi/chi/v5"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/save"
)

type App struct {
	Server *http.Server
}

func New(
	postSaver save.PostSaver,
	port int,
	timout time.Duration,
) *App {

	router := chi.NewRouter()

	router.Route("/create-post", func(r chi.Router) {
		r.Post("/", save.New(postSaver))
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
