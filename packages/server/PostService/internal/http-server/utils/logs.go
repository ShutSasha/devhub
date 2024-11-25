package utils

import (
	"fmt"
	"log/slog"
	"net/http"
)

func LogRequestBody(r *http.Request, log *slog.Logger) {
	const maxMemory = 10 << 20

	if err := r.ParseMultipartForm(maxMemory); err != nil {
		log.Error("failed to parse form-data", slog.Any("error", err.Error()))
		return
	}

	for key, values := range r.MultipartForm.Value {
		for _, value := range values {
			fmt.Printf("form-data field: key: %s, value: %s\n", key, value)
		}
	}

	for key, files := range r.MultipartForm.File {
		for _, file := range files {
			fmt.Printf("form-data file:\n")
			fmt.Printf("\tkey: %s\n", key)
			fmt.Printf("\tfilename: %s\n", file.Filename)
			fmt.Printf("\tsize: %d\n", file.Size)
			fmt.Printf("\tcontentType: %s\n", file.Header.Get("Content-Type"))
			fmt.Printf("\n")
		}
		fmt.Printf("\n")
	}
}
