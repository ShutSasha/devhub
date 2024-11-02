package s3

import (
	"bytes"
	"context"
	"fmt"
	"strings"

	"github.com/aws/aws-sdk-go-v2/aws"
	"github.com/aws/aws-sdk-go-v2/credentials"
	"github.com/aws/aws-sdk-go-v2/service/s3"
)

type FileStorage struct {
	bucket  string
	storage *s3.Client
}

func New(region, accessKey, secretKey, bucket string) (*FileStorage, error) {
	const op = "storage.s3.New"

	params := map[string]string{
		"region":    region,
		"accessKey": accessKey,
		"secretKey": secretKey,
		"bucket":    bucket,
	}

	missingParams := []string{}

	for name, value := range params {
		if value == "" {
			missingParams = append(missingParams, name)
		}
	}

	if len(missingParams) > 0 {
		return nil, fmt.Errorf("%v: The following parameters should not be empty: %s", op, strings.Join(missingParams, ", "))
	}

	options := s3.Options{
		Region:      region,
		Credentials: aws.NewCredentialsCache(credentials.NewStaticCredentialsProvider(accessKey, secretKey, "")),
	}

	client := s3.New(options)

	return &FileStorage{
		bucket:  bucket,
		storage: client,
	}, nil
}

func (fs *FileStorage) Save(ctx context.Context, key string, buf bytes.Buffer) error {
	const op = "storage.s3.Save"

	_, err := fs.storage.PutObject(ctx, &s3.PutObjectInput{
		Bucket: aws.String(fs.bucket),
		Key:    aws.String(key),
		Body:   bytes.NewReader(buf.Bytes()),
	})
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}

func (fs *FileStorage) Remove(ctx context.Context, key string) error {
	const op = "storage.s3.Remove"

	_, err := fs.storage.DeleteObject(ctx, &s3.DeleteObjectInput{
		Bucket: aws.String(fs.bucket),
		Key:    aws.String(key),
	})
	if err != nil {
		return fmt.Errorf("%s: %w", op, err)
	}

	return nil
}

func (fs *FileStorage) Get(ctx context.Context, key string) ([]byte, error) {
	const op = "storage.s3.Get"

	output, err := fs.storage.GetObject(ctx, &s3.GetObjectInput{
		Bucket: aws.String(fs.bucket),
		Key:    aws.String(key),
	})
	if err != nil {
		return nil, fmt.Errorf("%s: %w", op, err)
	}
	defer output.Body.Close()

	buf := new(bytes.Buffer)
	_, err = buf.ReadFrom(output.Body)
	if err != nil {
		return nil, fmt.Errorf("%s: failed to read object body: %w", op, err)
	}

	return buf.Bytes(), nil
}
