package models

import (
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

type Post struct {
	User        primitive.ObjectID   `json:"user" bson:"user"`
	Title       string               `json:"title" bson:"title"`
	Content     string               `json:"content" bson:"content"`
	CreatedAt   time.Time            `json:"createdAt" bson:"createdAt"`
	Likes       int                  `json:"likes" bson:"likes"`
	Dislikes    int                  `json:"dislikes" bson:"dislikes"`
	HeaderImage string               `json:"headerImage" bson:"headerImage"`
	Comments    []primitive.ObjectID `json:"comments" bson:"comments"`
	Tags        []string             `json:"tags" bson:"tags"`
}
