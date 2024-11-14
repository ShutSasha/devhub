package http

import (
	"errors"
	"fmt"
	"log/slog"
	"net/http"
	"strconv"
	"time"

	pb "github.com/ShutSasha/devhub/packages/server/CommentService/gen/go/post"
	handler "github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/handler/http"
	mwLogger "github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/handler/http/middleware/logger"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/port"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/rs/cors"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

type App struct {
	Server *http.Server
	port   int
}

func New(
	log *slog.Logger,
	svc port.CommentService,
	port int,
	timout time.Duration,
	postServicePort int,
) *App {
	conn, err := grpc.NewClient(
		fmt.Sprintf("localhost:%d", postServicePort),
		grpc.WithTransportCredentials(insecure.NewCredentials()),
	)
	if err != nil {
		panic("failed to connect to gRPC server")
	}

	grpcPostClient := pb.NewPostServiceClient(conn)
	commentHandler := handler.NewCommentHandler(svc, log, grpcPostClient)

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

	router.Route("/api/comments", func(r chi.Router) {
		r.Get("/{id}", commentHandler.GeById())
		r.Post("/", commentHandler.Create())
	})

	httpServer := &http.Server{
		Addr:         ":" + strconv.Itoa(port),
		Handler:      router,
		ReadTimeout:  timout,
		WriteTimeout: timout,
	}

	return &App{
		Server: httpServer,
		port:   port,
	}
}

func (a *App) Run() error {
	const op = "app.http.Run"

	if err := a.Server.ListenAndServe(); !errors.Is(err, http.ErrServerClosed) {
		return fmt.Errorf("%s: %w", op, err)
	}
	return nil
}
