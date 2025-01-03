package dtos

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type NotificationDto struct {
	ID        primitive.ObjectID `json:"id" bson:"_id"`
	Reciever  primitive.ObjectID `json:"reciever" bson:"reciever"`
	Sender    UserDto            `json:"sender" bson:"sender"`
	Content   string             `json:"content" bson:"content"`
	CreatedAt time.Time          `json:"createdAt" bson:"createdAt"`
}
