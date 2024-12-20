package dtos

type UserDto struct {
	ID       string `json:"id" bson:"_id"`
	Username string `json:"username" bson:"username"`
}
