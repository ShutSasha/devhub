package response

import (
	"fmt"

	"github.com/go-playground/validator/v10"
)

type Response struct {
	Status int                 `json:"status"`
	Errors map[string][]string `json:"errors"`
}

func Error(msgs map[string][]string, statusCode int) Response {
	return Response{
		Status: statusCode,
		Errors: msgs,
	}
}

func ValidationError(errs validator.ValidationErrors, statusCode int) Response {
	errMsgs := make(map[string][]string)

	for _, err := range errs {
		var message string
		switch err.Tag() {
		case "required":
			message = fmt.Sprintf("The %s field is required", err.Field())
		case "max":
			message = fmt.Sprintf("The %s field is too long", err.Field())
		case "min":
			message = fmt.Sprintf("The %s field is too short", err.Field())
		default:
			message = fmt.Sprintf("The %s field is not valid", err.Field())
		}

		errMsgs[err.Field()] = append(errMsgs[err.Field()], message)
	}

	return Response{
		Status: statusCode,
		Errors: errMsgs,
	}
}
