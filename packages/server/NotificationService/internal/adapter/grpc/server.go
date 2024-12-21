package grpc

import (
	"context"
	"fmt"
	"time"

	notifgen "github.com/ShutSasha/devhub/packages/server/NotificationService/gen/go/notification"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/domain/models"
	"go.mongodb.org/mongo-driver/bson/primitive"
	"google.golang.org/grpc"
)

type NotificationCreator interface {
	Create(ctx context.Context, notification models.Notification) (primitive.ObjectID, error)
}

type serverAPI struct {
	notifgen.UnimplementedNotificationServiceServer
	notificationCreator NotificationCreator
}

func Register(gRPCServer *grpc.Server, notificationCreator NotificationCreator) {
	notifgen.RegisterNotificationServiceServer(
		gRPCServer,
		&serverAPI{notificationCreator: notificationCreator},
	)
}

func (s *serverAPI) CreateNotification(
	ctx context.Context,
	req *notifgen.CreateNotificationRequest,
) (*notifgen.Response, error) {
	const op = "serverAPI.CreateNotification"
	recieverId, err := primitive.ObjectIDFromHex(req.Receiver)
	if err != nil {
		return &notifgen.Response{
			Success: false,
			Message: fmt.Sprintf("failed to parse receiver id: %s", err.Error()),
		}, fmt.Errorf("%s: %w", op, err)
	}

	senderId, err := primitive.ObjectIDFromHex(req.Sender)
	if err != nil {
		return &notifgen.Response{
			Success: false,
			Message: fmt.Sprintf("failed to parse sender id: %s", err.Error()),
		}, fmt.Errorf("%s: %w", op, err)
	}

	notification := models.Notification{
		Reciever:  recieverId,
		Sender:    senderId,
		Content:   req.Content,
		IsRead:    false,
		CreatedAt: time.Now(),
	}

	id, err := s.notificationCreator.Create(ctx, notification)
	if err != nil {
		return &notifgen.Response{
			Success: false,
			Message: fmt.Sprintf("failed to create notification: %s", err.Error()),
		}, fmt.Errorf("%s: %w", op, err)
	}

	return &notifgen.Response{
		Success: true,
		Message: fmt.Sprintf("notification created with id: %s", id.Hex()),
	}, nil
}
