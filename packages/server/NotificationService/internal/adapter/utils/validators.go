package utils

import "go.mongodb.org/mongo-driver/bson/primitive"

func IsValidObjectId(id string) error {
	_, err := primitive.ObjectIDFromHex(id)
	return err
}
