package dtos

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type CommentDto struct {
	Id          primitive.ObjectID `json:"_id" bson:"_id"`
	User        UserDto            `json:"user" bson:"user"`
	PostId      primitive.ObjectID `json:"post" bson:"post"`
	CommentText string             `json:"commentText" bson:"commentText"`
	CreatedAt   time.Time          `json:"createdAt" bson:"createdAt"`
}
