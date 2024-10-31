package app

import (
	"fmt"
	"log/slog"
	"time"

	pb "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/gen/go/user"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/app/httpapp"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage/mongodb"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

type App struct {
	HttpApp *httpapp.App
}

func New(
	log *slog.Logger,
	storagePath string,
	httpPort int,
	userSevicePort int,
	timeout time.Duration,
) *App {
	conn, err := grpc.NewClient(fmt.Sprintf("localhost:%d", userSevicePort), grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		panic("failed to connect to gRPC server")
	}
	grpcUserClient := pb.NewUserServiceClient(conn)

	storage, err := mongodb.New(storagePath)
	if err != nil {
		panic(err)
	}

	httpApp := httpapp.New(
		log,
		storage,
		grpcUserClient,
		httpPort,
		timeout,
	)

	return &App{
		HttpApp: httpApp,
	}
}
