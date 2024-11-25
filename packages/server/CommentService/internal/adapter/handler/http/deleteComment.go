package http

import (
	"context"
	"fmt"
	"log/slog"
	"net/http"

	pb "github.com/ShutSasha/devhub/packages/server/CommentService/gen/go/post"
	ub "github.com/ShutSasha/devhub/packages/server/CommentService/gen/go/user"
	"github.com/ShutSasha/devhub/packages/server/CommentService/internal/adapter/utils"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
)

// @Summary      Delete a comment by ID
// @Description  Delete a comment using its unique ID.
// @Tags         comments
// @Accept       json
// @Produce      json
// @Param        id path string true "Comment ID"
// @Success      200 {object} map[string]interface{} "Comment successfully deleted"
// @Failure      500 {object} map[string]interface{} "failed to delete comment"
// @Router       /api/comments/{id} [delete]
func (ch *CommentHandler) Delete() http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handler.http.Delete"
		defer r.Body.Close()

		log := ch.logger.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		id := chi.URLParam(r, "id")
		if err := utils.IsValidObjectId(id); err != nil {
			utils.HandleError(log, w, r, "provided id is not correct", err,
				http.StatusBadRequest, "id", "provided id is not correct")
			return
		}

		comment, err := ch.svc.GetById(context.TODO(), id)
		if err != nil {
			utils.HandleError(log, w, r, "failed to find comment", err,
				http.StatusNotFound, "comment", "failed to find comment")
			return
		}

		err = ch.svc.Delete(context.TODO(), id)
		if err != nil {
			utils.HandleError(log, w, r, "failed to delete comment", err,
				http.StatusInternalServerError, "comment", "Failed to delete comment")
			return
		}

		resp, err := ch.grpcPostClient.RemoveCommentFromPost(context.TODO(), &pb.RemoveCommentRequest{
			PostId:    comment.PostId.Hex(),
			CommentId: id,
		})
		if err != nil {
			utils.HandleError(log, w, r, "failed to remove comment from post", err,
				http.StatusInternalServerError, "comment", "failed to remove comment from post")
			return
		}
		if resp != nil {
			if !resp.Success {
				utils.HandleError(log, w, r, "failed to remove comment from post",
					fmt.Errorf("%s: %s", op, resp.Message), http.StatusInternalServerError, "comment", "failed to remove comment from post")
				return
			}
		}

		delFromUserResp, err := ch.grpcUserClient.DeleteCommentFromUser(context.TODO(), &ub.DeleteCommentRequest{
			UserId:    comment.User.Id.Hex(),
			CommentId: id,
		})
		if err != nil {
			utils.HandleError(log, w, r, "failed to remove comment from user", err,
				http.StatusInternalServerError, "comment", "failed to remove comment from user")
			return
		}
		if delFromUserResp != nil {
			if !delFromUserResp.Success {
				utils.HandleError(log, w, r, "failed to remove comment from user",
					fmt.Errorf("%s: %s", op, delFromUserResp.Message), http.StatusInternalServerError, "comment", "failed to remove comment from user")
				return
			}
		}

		log.Info("comment successfully deleted")
		w.WriteHeader(http.StatusOK)
		render.JSON(w, r, map[string]interface{}{
			"Status":  http.StatusOK,
			"Message": "Successfully deleted",
		})
	}
}
