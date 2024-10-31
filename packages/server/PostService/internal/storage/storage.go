package storage

import (
	"errors"
	"time"

	"go.mongodb.org/mongo-driver/bson/primitive"
)

var (
	ErrPostNotFound  = errors.New("post not found")
	ErrPostsNotFound = errors.New("posts not found")
)

type PostModel struct {
	Id          primitive.ObjectID `json:"_id" bson:"_id"`
	User        primitive.ObjectID `json:"user" bson:"user"`
	Title       string             `json:"title" bson:"title"`
	Content     string             `json:"content" bson:"content"`
	CreatedAt   time.Time          `json:"created_at" bson:"created_at"`
	Likes       int                `json:"likes" bson:"likes"`
	Dislikes    int                `json:"dislikes" bson:"dislikes"`
	HeaderImage string             `json:"header_image" bson:"header_image"`
	Comments    []CommentModel     `json:"comments" bson:"comments"`
	Tags        []string           `json:"tags" bson:"tags"`
}

type CommentModel struct {
	Id          primitive.ObjectID `json:"_id" bson:"_id"`
	User        primitive.ObjectID `json:"user" bson:"user"`
	CommentText string             `json:"comment_text" bson:"comment_text"`
	Likes       int                `json:"likes" bson:"likes"`
	CreatedAt   time.Time          `json:"created_at" bson:"created_at"`
}
