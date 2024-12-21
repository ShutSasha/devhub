package dtos

import "go.mongodb.org/mongo-driver/bson/primitive"

type UserDto struct {
	ID       primitive.ObjectID `json:"id" bson:"_id"`
	Username string             `json:"username" bson:"username"`
}
