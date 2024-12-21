package models

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type Notification struct {
	Reciever  primitive.ObjectID `json:"reciever" bson:"reciever"`
	Sender    primitive.ObjectID `json:"sender" bson:"sender"`
	Content   string             `json:"content" bson:"content"`
	IsRead    bool               `json:"isRead" bson:"isRead"`
	CreatedAt time.Time          `json:"createdAt" bson:"createdAt"`
}
