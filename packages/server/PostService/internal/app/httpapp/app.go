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
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/search"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/update"
	mwLogger "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/middleware/logger"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/rs/cors"
	httpSwagger "github.com/swaggo/http-swagger/v2"
)

type App struct {
	log    *slog.Logger
	Server *http.Server
	port   int
}

type PostStorage interface {
	save.PostSaver
	get.PostProvider
	delete.PostRemover
	update.PostUpdater
	search.PostSearcher
}

func New(
	log *slog.Logger,
	postStorage PostStorage,
	port int,
	timout time.Duration,
) *App {
	corsHandler := cors.New(cors.Options{
		AllowedOrigins:   []string{"http://localhost:5295"},
		AllowedMethods:   []string{"GET", "POST", "PATCH", "DELETE"},
		AllowedHeaders:   []string{"Authorization", "Content-Type"},
		AllowCredentials: true,
	})

	router := chi.NewRouter()

	router.Use(corsHandler.Handler)
	router.Use(middleware.RequestID)
	router.Use(mwLogger.New(log))
	router.Use(middleware.Recoverer)
	router.Use(middleware.URLFormat)

	router.Get("/swagger/*", httpSwagger.Handler(
		httpSwagger.URL(fmt.Sprintf("http://localhost:%d/swagger/doc.json", port)),
	))

	router.Route("/api/posts", func(r chi.Router) {
		r.Get("/{id}", get.New(log, postStorage))
		r.Post("/", save.New(log, postStorage))
		r.Delete("/{id}", delete.New(log, postStorage))
		r.Patch("/{id}", update.New(log, postStorage))

		r.Get("/search", search.New(log, postStorage))
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
