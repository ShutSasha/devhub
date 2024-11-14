package models

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type Comment struct {
	User        primitive.ObjectID `json:"user" bson:"user"`
	Post        primitive.ObjectID `json:"post" bson:"post"`
	CommentText string             `json:"commentText" bson:"commentText"`
	CreatedAt   time.Time          `json:"createdAt" bson:"createdAt"`
}
