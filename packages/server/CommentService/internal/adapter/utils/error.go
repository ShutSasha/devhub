package utils

import (
	"log/slog"
	"net/http"

	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/utils/api"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/lib/logger/sl"
	"github.com/go-chi/render"
	"github.com/go-playground/validator/v10"
)

func HandleError(
	log *slog.Logger,
	w http.ResponseWriter,
	r *http.Request,
	logMessage string,
	err error,
	statusCode int,
	key string,
	userMessage string,
) {
	if err != nil {
		log.Error(logMessage, sl.Err(err))
	} else {
		log.Error(logMessage)
	}

	w.WriteHeader(statusCode)

	render.JSON(w, r, api.Error(map[string][]string{
		key: {userMessage},
	}, statusCode))
}

func HandleValidatorError(
	log *slog.Logger,
	w http.ResponseWriter,
	r *http.Request,
	logMessage string,
	err error,
	validateErr validator.ValidationErrors,
	statusCode int,
) {
	log.Error(logMessage, sl.Err(err))
	w.WriteHeader(statusCode)
	render.JSON(w, r, api.ValidationError(
		validateErr,
		statusCode,
	))
}
