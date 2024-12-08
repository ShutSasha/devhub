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
	User        UserModel          `json:"user" bson:"user"`
	Title       string             `json:"title" bson:"title"`
	Content     string             `json:"content" bson:"content"`
	CreatedAt   time.Time          `json:"createdAt" bson:"createdAt"`
	Likes       int                `json:"likes" bson:"likes"`
	Dislikes    int                `json:"dislikes" bson:"dislikes"`
	Saved       int                `json:"saved" bson:"saved"`
	HeaderImage string             `json:"headerImage" bson:"headerImage"`
	Comments    []CommentModel     `json:"comments" bson:"comments"`
	Tags        []string           `json:"tags" bson:"tags"`
}

type CommentModel struct {
	Id          primitive.ObjectID `json:"_id" bson:"_id"`
	User        UserModel          `json:"user" bson:"user"`
	Post        primitive.ObjectID `json:"post" bson:"post"`
	CommentText string             `json:"commentText" bson:"commentText"`
	CreatedAt   time.Time          `json:"createdAt" bson:"createdAt"`
}

type UserModel struct {
	Id        primitive.ObjectID `json:"_id" bson:"_id"`
	Name      string             `json:"name" bson:"name"`
	UserName  string             `json:"username" bson:"username"`
	Avatar    string             `json:"avatar" bson:"avatar"`
	DevPoints int                `json:"devPoints" bson:"devPoints"`
}
