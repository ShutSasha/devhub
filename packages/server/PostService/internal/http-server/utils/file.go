package utils

import (
	"bytes"
	"context"
	"io"
	"log/slog"
	"net/http"
	"strconv"
	"time"

	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/sl"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

type FileSaver interface {
	Save(
		ctx context.Context,
		key string,
		buf bytes.Buffer,
	) error
}

func HandleFileUpload(
	log *slog.Logger,
	r *http.Request,
	userId primitive.ObjectID,
	fileSaver FileSaver,
) (string, error) {
	file, fileHeader, err := r.FormFile("headerImage")
	if err != nil {
		return "", err
	}
	defer file.Close()

	var buf bytes.Buffer
	if _, err := io.Copy(&buf, file); err != nil {
		log.Error("failed to read header image", sl.Err(err))
		return "", err
	}

	timestamp := time.Now().Unix()
	imageKey := "post_images/" + userId.Hex() + "/" + strconv.FormatInt(timestamp, 10) + "_" + fileHeader.Filename
	if err := fileSaver.Save(context.TODO(), imageKey, buf); err != nil {
		log.Error("failed to save header image", sl.Err(err))
		return "", err
	}
	return imageKey, nil
}
