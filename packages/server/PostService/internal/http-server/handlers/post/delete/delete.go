package delete

import (
	"context"
	"fmt"
	"log/slog"
	"net/http"

	pb "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/gen/go/user"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/interfaces"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/utils"
	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/sl"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

// New is a handler function that processes the HTTP request to delete a post by its ID.
// It validates the post ID, checks for errors, and calls the PostRemover to delete the post.
// @Summary Delete a post by ID
// @Description This endpoint allows a user to delete a post by its unique ID.
// @Tags posts
// @Accept json
// @Produce json
// @Param id path string true "Post ID"
// @Success 200 {object} map[string]interface{} "Success message"
// @Failure 400 {object} map[string]interface{} "Invalid postId format"
// @Failure 500 {object} map[string]interface{} "Internal server error"
// @Router /api/posts/{id} [delete]
func New(
	log *slog.Logger,
	postRemover interfaces.PostRemover,
	postProvider interfaces.PostProvider,
	fileRemover interfaces.FileRemover,
	fileProvider interfaces.FileProvider,
	grpcClient pb.UserServiceClient,
) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.delete.New"
		defer r.Body.Close()

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", middleware.GetReqID(r.Context())),
		)

		id := chi.URLParam(r, "id")

		postId, err := primitive.ObjectIDFromHex(id)
		if err != nil {
			utils.HandleError(log, w, r, "failed to create objectId from postId", err, http.StatusBadRequest, "postId", "Invalid postId format")
			return
		}

		post, err := postProvider.GetById(context.TODO(), postId, fileProvider)
		if err == nil && post.HeaderImage != "" {
			fileRemoveErr := fileRemover.Remove(context.TODO(), post.HeaderImage)
			if fileRemoveErr != nil {
				log.Error("failed to delete post image from aws", sl.Err(fileRemoveErr))
			} else {
				log.Info("header image successfuly deleted from aws")
			}
		} else if err != nil {
			log.Error("failed to get post by postId", sl.Err(err))
		}

		grpcDeleteResponse, err := grpcClient.DeletePostFromUser(context.TODO(), &pb.DeletePostRequest{
			PostId: id,
		})
		if err != nil {
			utils.HandleError(log, w, r, "failed to notify user service", err, http.StatusInternalServerError, "userService", "Failed to notify user service")
			return
		}
		if !grpcDeleteResponse.Success {
			utils.HandleError(log, w, r, "user service returned failure", fmt.Errorf(grpcDeleteResponse.Message), http.StatusInternalServerError,
				"userService", "User service returned failure: "+grpcDeleteResponse.Message)
			return
		}

		log.Info("post succesfully removed from user")

		err = postRemover.Remove(
			context.TODO(),
			postId,
		)
		if err != nil {
			log.Error("can not delete post", sl.Err(err))
			log.Info("rolling back post")

			_, rollbackErr := grpcClient.RestoreUserPost(context.TODO(), &pb.RestorePostRequest{
				UserId: post.User.Id.Hex(),
				PostId: postId.Hex(),
			})
			if rollbackErr != nil {
				utils.HandleError(log, w, r, "failed to rollback post in user service", rollbackErr, http.StatusInternalServerError,
					"rollback", "Failed to rollback post in user service: "+rollbackErr.Error())
				return
			}

			log.Info("successfully rolled back deletion in user service")

			render.JSON(w, r, resp.Error(map[string][]string{
				"post": {err.Error()},
			}, http.StatusInternalServerError))

			return
		}

		log.Info("post successfully deleted", slog.Any("id", postId))

		render.JSON(w, r, map[string]interface{}{
			"Status":  http.StatusOK,
			"Message": "Successfully deleted",
		})
	}
}
