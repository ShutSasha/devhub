package http

import (
	"errors"
	"fmt"
	"log/slog"
	"net/http"
	"strconv"
	"time"

	_ "github.com/ShutSasha/devhub/packages/server/CommentService/docs"
	pb "github.com/ShutSasha/devhub/packages/server/CommentService/gen/go/post"
	ub "github.com/ShutSasha/devhub/packages/server/CommentService/gen/go/user"
	handler "github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/handler/http"
	mwLogger "github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/handler/http/middleware/logger"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/port"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/rs/cors"
	httpSwagger "github.com/swaggo/http-swagger/v2"
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
	userServicePort int,
) *App {
	corsHandler := cors.New(cors.Options{
		AllowedOrigins:   []string{"http://localhost:5295"},
		AllowedMethods:   []string{"GET", "POST", "PATCH", "DELETE"},
		AllowedHeaders:   []string{"Authorization", "Content-Type"},
		AllowCredentials: true,
	})

	connPost, err := grpc.NewClient(
		fmt.Sprintf("localhost:%d", postServicePort),
		grpc.WithTransportCredentials(insecure.NewCredentials()),
	)
	if err != nil {
		panic("failed to connect to post gRPC server")
	}
	grpcPostClient := pb.NewPostServiceClient(connPost)

	connUser, err := grpc.NewClient(
		fmt.Sprintf("localhost:%d", userServicePort),
		grpc.WithTransportCredentials(insecure.NewCredentials()),
	)
	if err != nil {
		panic("failed to connect to user gRPC server")
	}
	grpcUserClient := ub.NewUserServiceClient(connUser)

	commentHandler := handler.NewCommentHandler(svc, log, grpcPostClient, grpcUserClient)

	router := chi.NewRouter()

	router.Use(corsHandler.Handler)
	router.Use(middleware.RequestID)
	router.Use(mwLogger.New(log))
	router.Use(middleware.Recoverer)
	router.Use(middleware.URLFormat)

	router.Get("/swagger/*", httpSwagger.Handler(
		httpSwagger.URL(fmt.Sprintf("http://localhost:%d/swagger/doc.json", port)),
	))

	router.Route("/api/comments", func(r chi.Router) {
		r.Get("/{id}", commentHandler.GetById())
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
