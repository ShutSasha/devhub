package response

import (
	"fmt"
	"strings"

	"github.com/go-playground/validator/v10"
)

type Response struct {
	Status int    `json:"status"`
	Error  string `json:"error,omitempty"`
}

func Error(msg string, statusCode int) Response {
	return Response{
		Status: statusCode,
		Error:  msg,
	}
}

func ValidationError(errs validator.ValidationErrors, statusCode int) Response {
	var errMsgs []string

	for _, err := range errs {
		switch err.ActualTag() {
		case "required":
			errMsgs = append(errMsgs, fmt.Sprintf("field %s is a required field", err.Field()))
		default:
			errMsgs = append(errMsgs, fmt.Sprintf("field %s is not valid", err.Field()))
		}
	}

	return Response{
		Status: statusCode,
		Error:  strings.Join(errMsgs, ", "),
	}
}
