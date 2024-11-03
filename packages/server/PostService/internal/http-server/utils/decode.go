package utils

import (
	"encoding/json"
	"errors"
	"fmt"
	"io"
	"log/slog"
	"net/http"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/sl"
)

func DecodeRequestBody(log *slog.Logger, r *http.Request, req interface{}) error {
	if err := json.NewDecoder(r.Body).Decode(req); err != nil {
		if errors.Is(err, io.EOF) {
			log.Error("request body is empty")
			return fmt.Errorf("empty request")
		}
		log.Error("failed to decode request body", sl.Err(err))
		return fmt.Errorf("failed to decode request")
	}
	return nil
}
