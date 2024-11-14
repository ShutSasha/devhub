package app

import (
	"fmt"
	"log/slog"
	"time"

	pb "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/gen/go/user"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/app/grpcapp"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/app/httpapp"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/services"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage/mongodb"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage/s3"
	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"
)

type App struct {
	HttpApp *httpapp.App
	GRPCApp *grpcapp.App
}

func New(
	userServicePort int,
	storagePath string,
	log *slog.Logger,
	awsRegion string,
	accessKey string,
	secretKey string,
	bucket string,
	httpPort int,
	timeout time.Duration,
	grpcPort int,
) *App {
	conn, err := grpc.NewClient(
		fmt.Sprintf("localhost:%d", userServicePort),
		grpc.WithTransportCredentials(insecure.NewCredentials()),
	)
	if err != nil {
		panic("failed to connect to gRPC server")
	}

	dbStorage, err := mongodb.New(storagePath)
	if err != nil {
		panic(err)
	}

	fileStorage, err := s3.New(awsRegion, accessKey, secretKey, bucket)
	if err != nil {
		panic(err)
	}

	grpcUserClient := pb.NewUserServiceClient(conn)

	postService := services.New(dbStorage)

	grpcApp := grpcapp.New(log, postService, grpcPort)

	httpApp := httpapp.New(
		log,
		dbStorage,
		fileStorage,
		grpcUserClient,
		httpPort,
		timeout,
	)

	return &App{
		HttpApp: httpApp,
		GRPCApp: grpcApp,
	}
}
