package repository

import (
	"context"
	"fmt"

	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/adapter/storage/mongodb"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/domain/dtos"
	"github.com/ShutSasha/devhub/packages/server/NotificationService/internal/core/domain/models"
	"go.mongodb.org/mongo-driver/bson"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type NotificationRepository struct {
	storage *mongodb.Storage
}

func NewNotificationRepository(storage *mongodb.Storage) *NotificationRepository {
	return &NotificationRepository{
		storage,
	}
}

func (r *NotificationRepository) GetNotifications(ctx context.Context, userId primitive.ObjectID, limit, page int) ([]dtos.NotificationDto, error) {
	const op = "NotificationRepository.GetNotifications"

	collection := r.storage.Database("DevHubDB").Collection("notifications")
	pipeline := bson.A{
		bson.D{
			{Key: "$match", Value: bson.D{
				{Key: "reciever", Value: userId},
			}},
		},
		bson.D{
			{Key: "$lookup", Value: bson.D{
				{Key: "from", Value: "users"},
				{Key: "localField", Value: "sender"},
				{Key: "foreignField", Value: "_id"},
				{Key: "as", Value: "senderDetails"},
			}},
		},
		bson.D{
			{Key: "$unwind", Value: bson.D{
				{Key: "path", Value: "$senderDetails"},
				{Key: "preserveNullAndEmptyArrays", Value: true},
			}},
		},
		bson.D{
			{Key: "$project", Value: bson.D{
				{Key: "reciever", Value: 1},
				{Key: "sender", Value: "$senderDetails"},
				{Key: "content", Value: 1},
				{Key: "createdAt", Value: 1},
			}},
		},
		bson.D{
			{Key: "$sort", Value: bson.D{
				{Key: "createdAt", Value: -1},
			}},
		},
		bson.D{
			{Key: "$skip", Value: int64((page - 1) * limit)},
		},
		bson.D{
			{Key: "$limit", Value: int64(limit)},
		},
	}

	cursor, err := collection.Aggregate(ctx, pipeline)
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}
	defer cursor.Close(ctx)

	var notifications []dtos.NotificationDto
	if err := cursor.All(ctx, &notifications); err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}

	return notifications, nil
}

func (r *NotificationRepository) Create(ctx context.Context, notification models.Notification) (primitive.ObjectID, error) {
	const op = "NotificationRepository.Create"

	collection := r.storage.Database("DevHubDB").Collection("notifications")

	res, err := collection.InsertOne(ctx, notification)
	if err != nil {
		return primitive.NilObjectID, fmt.Errorf("%s: %w", op, err)
	}

	return res.InsertedID.(primitive.ObjectID), nil
}

func (r *NotificationRepository) GetByID(ctx context.Context, id primitive.ObjectID) (dtos.NotificationDto, error) {
	const op = "NotificationRepository.GetByID"

	collection := r.storage.Database("DevHubDB").Collection("notifications")

	var notification dtos.NotificationDto
	if err := collection.FindOne(ctx, bson.M{"_id": id}).Decode(&notification); err != nil {
		return dtos.NotificationDto{}, fmt.Errorf("%s: %w", op, err)
	}

	return notification, nil
}
