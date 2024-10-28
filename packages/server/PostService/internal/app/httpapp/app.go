package httpapp

import (
	"errors"
	"fmt"
	"log/slog"
	"net/http"
	"strconv"
	"time"

	_ "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/docs"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/delete"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/get"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/save"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/update"
	mwLogger "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/middleware/logger"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	httpSwagger "github.com/swaggo/http-swagger/v2"
)

type App struct {
	log    *slog.Logger
	Server *http.Server
	port   int
}

func New(
	log *slog.Logger,
	postSaver save.PostSaver,
	postProvider get.PostProvider,
	postRemover delete.PostRemover,
	postUpdater update.PostUpdater,
	port int,
	timout time.Duration,
) *App {
	router := chi.NewRouter()

	router.Use(middleware.RequestID)
	router.Use(middleware.Logger)
	router.Use(mwLogger.New(log))
	router.Use(middleware.Recoverer)
	router.Use(middleware.URLFormat)

	router.Get("/swagger/*", httpSwagger.Handler(
		httpSwagger.URL(fmt.Sprintf("http://localhost:%d/swagger/doc.json", port)),
	))

	router.Route("/api/posts", func(r chi.Router) {
		r.Post("/", save.New(log, postSaver))

		r.Get("/{id}", get.New(log, postProvider))

		r.Delete("/{id}", delete.New(log, postRemover))

		r.Patch("/{id}", update.New(log, postUpdater))
	})

	httpServer := &http.Server{
		Addr:         ":" + strconv.Itoa(port),
		Handler:      router,
		ReadTimeout:  timout,
		WriteTimeout: timout,
	}

	return &App{
		log:    &slog.Logger{},
		Server: httpServer,
		port:   port,
	}
}

func (a *App) Run() error {
	const op = "httpapp.Run"

	if err := a.Server.ListenAndServe(); !errors.Is(err, http.ErrServerClosed) {
		return fmt.Errorf("%s: %w", op, err)
	}
	return nil
}
