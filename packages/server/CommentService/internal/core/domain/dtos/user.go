package dtos

import "go.mongodb.org/mongo-driver/bson/primitive"

type UserDto struct {
	Id       primitive.ObjectID `json:"_id" bson:"_id"`
	UserName string             `json:"username" bson:"username"`
	Avatar   string             `json:"avatar" bson:"avatar"`
}