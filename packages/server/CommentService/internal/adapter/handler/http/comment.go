package http

import (
	"context"
	"encoding/json"
	"log/slog"
	"net/http"

	pb "github.com/ShutSasha/devhub/packages/server/CommentService/gen/go/post"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/utils"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/port"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
)

type CommentHandler struct {
	logger     *slog.Logger
	svc        port.CommentService
	grpcClient pb.PostServiceClient
}

func NewCommentHandler(svc port.CommentService, log *slog.Logger, grpcClient pb.PostServiceClient) *CommentHandler {
	return &CommentHandler{log, svc, grpcClient}
}

type createCommentRequest struct {
	UserId  string `json:"userId"`
	PostId  string `json:"postId"`
	Content string `json:"content"`
}

func (ch *CommentHandler) Create() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handler.http.Create"
		defer r.Body.Close()

		log := ch.logger.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		var request createCommentRequest
		if err := json.NewDecoder(r.Body).Decode(&request); err != nil {
			utils.HandleError(log, w, r, "failed to devode request body",
				err, http.StatusBadRequest, "comment", err.Error())
			return
		}

		commentID, err := ch.svc.Create(context.TODO(), request.UserId, request.PostId, request.Content)
		if err != nil {
			utils.HandleError(log, w, r, "failed to create comment",
				err, http.StatusInternalServerError, "comment", "Failed to create comment")
			return
		}

		comment, err := ch.svc.GetById(context.TODO(), commentID.Hex())
		if err != nil {
			utils.HandleError(log, w, r, "failed to get comment after creating",
				err, http.StatusInternalServerError, "comment", "Failed to create comment")
			return
		}

		_, err = ch.grpcClient.AddCommentToUser(context.TODO(), &pb.AddCommentRequest{
			PostId:    comment.PostId.Hex(),
			CommentId: commentID.Hex(),
		})
		if err != nil {
			utils.HandleError(log, w, r, "failed to comment to post",
				err, http.StatusInternalServerError, "comment", "Failed to create comment")
			return
		}

		log.Info("comment successfully added")
		w.WriteHeader(http.StatusCreated)
		render.JSON(w, r, comment)
	}
}

func (ch *CommentHandler) GeById() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handler.http.GeById"
		defer r.Body.Close()

		log := ch.logger.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		id := chi.URLParam(r, "id")

		comment, err := ch.svc.GetById(context.TODO(), id)
		if err != nil {
			utils.HandleError(log, w, r, "failed to get comment", err,
				http.StatusInternalServerError, "comment", "Failed to get comment")
			return
		}

		log.Info("comment successfully retrieved")
		w.WriteHeader(http.StatusCreated)
		render.JSON(w, r, comment)
	}
}
