package http

import (
	"context"
	"encoding/json"
	"fmt"
	"log/slog"
	"net/http"

	pb "github.com/ShutSasha/devhub/packages/server/CommentService/gen/go/post"
	ub "github.com/ShutSasha/devhub/packages/server/CommentService/gen/go/user"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/utils"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/core/port"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
)

type CommentHandler struct {
	logger         *slog.Logger
	svc            port.CommentService
	grpcPostClient pb.PostServiceClient
	grpcUserClient ub.UserServiceClient
}

func NewCommentHandler(svc port.CommentService, log *slog.Logger,
	grpcPostClient pb.PostServiceClient, grpcUserClient ub.UserServiceClient) *CommentHandler {
	return &CommentHandler{log, svc, grpcPostClient, grpcUserClient}
}

type createCommentRequest struct {
	UserId  string `json:"userId"`
	PostId  string `json:"postId"`
	Content string `json:"content"`
}

// @Summary      Create a new comment
// @Description  This endpoint allows a user to create a new comment on a post.
// @Tags         comments
// @Accept       json
// @Produce      json
// @Param        request body createCommentRequest true "Create Comment Request"
// @Success      201 {object} map[string]interface{} "Comment successfully created"
// @Failure      400 {object} map[string]interface{} "Invalid request body"
// @Failure      500 {object} map[string]interface{} "Failed to create comment"
// @Router       /api/comments [post]
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

		_, err = ch.grpcPostClient.AddCommentToPost(context.TODO(), &pb.AddCommentRequest{
			PostId:    comment.PostId.Hex(),
			CommentId: commentID.Hex(),
		})
		if err != nil {
			utils.HandleError(log, w, r, "failed to comment to post",
				err, http.StatusInternalServerError, "comment", "Failed to create comment")
			return
		}

		resp, err := ch.grpcUserClient.AddCommentToUser(context.TODO(), &ub.AddCommentRequest{
			Id:        comment.User.Id.Hex(),
			CommentId: commentID.Hex(),
		})
		if resp != nil {
			if !resp.Success {
				utils.HandleError(log, w, r, "failed to add comment to user",
					fmt.Errorf("%s: %s", op, resp.Message), http.StatusInternalServerError, "comment", "failed to add comment to user")
				return
			}
		}
		if err != nil {
			utils.HandleError(log, w, r, "failed to add comment to user",
				err, http.StatusInternalServerError, "comment", "failed to add comment to user")
			return
		}

		log.Info("comment successfully added")
		w.WriteHeader(http.StatusCreated)
		render.JSON(w, r, comment)
	}
}

// @Summary      Retrieve a comment by ID
// @Description  Fetch a comment using its unique ID.
// @Tags         comments
// @Accept       json
// @Produce      json
// @Param        id path string true "Comment ID"
// @Success      200 {object} map[string]interface{} "Comment retrieved successfully"
// @Failure      404 {object} map[string]interface{} "Comment not found"
// @Failure      500 {object} map[string]interface{} "Failed to retrieve comment"
// @Router       /api/comments/{id} [get]
func (ch *CommentHandler) GetById() http.HandlerFunc {
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
