package models

import "go.mongodb.org/mongo-driver/bson/primitive"

type Comment struct {
	Post        primitive.ObjectID `json:"post" bson:"post"`
	User        primitive.ObjectID `json:"user" bson:"user"`
	CommentText string             `json:"commentText" bson:"commentText"`
	Likes       int                `json:"likes" bson:"likes"`
}
