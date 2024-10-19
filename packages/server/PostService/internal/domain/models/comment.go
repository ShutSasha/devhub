package models

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type Comment struct {
	User        primitive.ObjectID `json:"user" bson:"user"`
	CommentText string             `json:"comment_text" bson:"comment_text"`
	Likes       int                `json:"likes" bson:"likes"`
	CreatedAt   time.Time          `json:"created_at" bson:"created_at"`
}
