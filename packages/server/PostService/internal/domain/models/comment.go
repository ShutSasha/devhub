package models

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type Comment struct {
	User        primitive.ObjectID `json:"user" bson:"user"`
	CommentText string             `json:"commentText" bson:"commentText"`
	Likes       int                `json:"likes" bson:"likes"`
	CreatedAt   time.Time          `json:"createdAt" bson:"createdAt"`
}
