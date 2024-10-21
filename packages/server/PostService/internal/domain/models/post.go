package models

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type Post struct {
	User        primitive.ObjectID `json:"user" bson:"user"`
	Title       string             `json:"title" bson:"title"`
	Description string             `json:"description" bson:"description"`
	CreatedAt   time.Time          `json:"created_at" bson:"created_at"`
	Likes       int                `json:"likes" bson:"likes"`
	Dislikes    int                `json:"dislikes" bson:"dislikes"`
	Images      []string           `json:"images" bson:"images"`
	Comments    []Comment          `json:"comments" bson:"comments"`
	Tags        []string           `json:"tags" bson:"tags"`
}
