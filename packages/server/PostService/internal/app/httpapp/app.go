package httpapp

import (
	"context"
	"errors"
	"fmt"
	"log/slog"
	"net/http"
	"strconv"
	"time"

	_ "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/docs"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/models"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/delete"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/get"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/save"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/search"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/handlers/post/update"
	mwLogger "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/middleware/logger"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	httpSwagger "github.com/swaggo/http-swagger/v2"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type App struct {
	log    *slog.Logger
	Server *http.Server
	port   int
}

type PostSaver interface {
	Save(
		ctx context.Context,
		userId primitive.ObjectID,
		title string,
		content string,
		tags []string,
	) (primitive.ObjectID, error)
}

type PostProvider interface {
	GetById(
		ctx context.Context,
		postId primitive.ObjectID,
	) (*models.Post, error)
}

type PostRemover interface {
	Delete(
		ctx context.Context,
		postId primitive.ObjectID,
	) error
}

type PostUpdater interface {
	Update(
		ctx context.Context,
		postId primitive.ObjectID,
		title string,
		content string,
		headerImage string,
		tags []string,
	) error
}

type PostSearcher interface {
	Search(
		ctx context.Context,
		sortBy string,
		query string,
		tags []string,
	) ([]models.Post, error)
}

type PostStorage interface {
	PostSaver
	PostProvider
	PostRemover
	PostUpdater
	PostSearcher
}

func New(
	log *slog.Logger,
	postStorage PostStorage,
	port int,
	timout time.Duration,
) *App {
	router := chi.NewRouter()

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
