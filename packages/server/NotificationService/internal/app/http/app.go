package http

import (
	"errors"
	"fmt"
	"log/slog"
	"net/http"
	"strconv"
	"time"

	_ "github.com/ShutSasha/devhub/packages/server/NotificationService/docs"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/adapter/http/handlers"
	mwLogger "github.com/ShutSasha/devhub/packages/server/NotificationService/internal/adapter/http/middleware/logger"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/port"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/rs/cors"
	httpSwagger "github.com/swaggo/http-swagger/v2"
)

type App struct {
	Server *http.Server
	port   int
}

func New(
	log *slog.Logger,
	svc port.NotificationService,
	port int,
	timout time.Duration,
) *App {
	corsHandler := cors.New(cors.Options{
		AllowedOrigins:   []string{"http://localhost:5295"},
		AllowedMethods:   []string{"GET", "POST", "PATCH", "DELETE"},
		AllowedHeaders:   []string{"Authorization", "Content-Type"},
		AllowCredentials: true,
	})

	notificationHandler := handlers.NewNotificationHandler(svc, log)

	router := chi.NewRouter()

	router.Use(corsHandler.Handler)
	router.Use(middleware.RequestID)
	router.Use(mwLogger.New(log))
	router.Use(middleware.Recoverer)
	router.Use(middleware.URLFormat)

	router.Get("/swagger/*", httpSwagger.Handler(
		httpSwagger.URL(fmt.Sprintf("http://localhost:%d/swagger/doc.json", port)),
	))

	router.Route("/api/notifications", func(r chi.Router) {
		r.Get("/{user_id}", notificationHandler.GetNotifications)
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
