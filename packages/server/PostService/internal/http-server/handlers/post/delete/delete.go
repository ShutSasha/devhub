package delete

import (
	"context"
	"log/slog"
	"net/http"

	pb "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/gen/go/user"
	resp "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/api/response"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/lib/logger/sl"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/storage"
	"github.com/go-chi/chi/v5"
	"github.com/go-chi/chi/v5/middleware"
	"github.com/go-chi/render"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

// PostRemover is an interface that defines the method for deleting a post by its ID.
// Delete takes a context and postId, and returns an error if the deletion fails.
type PostRemover interface {
	Delete(
		ctx context.Context,
		postId primitive.ObjectID,
	) error
}

// PostProvider is an interface that defines the method for retrieving a post by its ID.
// GetPostById takes a context and postId, and returns the post model or an error.
type PostProvider interface {
	GetById(
		ctx context.Context,
		postId primitive.ObjectID,
	) (*storage.PostModel, error)
}

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
	postRemover PostRemover,
	postProvider PostProvider,
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
			log.Error("failed to create objectId from postId", sl.Err(err))

			render.JSON(w, r, resp.Error(
				map[string][]string{"postId": {"Invalid postId format"}},
				http.StatusBadRequest,
			))

			return
		}

		grpcDeleteResponse, err := grpcClient.DeletePostFromUser(context.TODO(), &pb.DeletePostRequest{
			PostId: id,
		})
		if err != nil {
			log.Error("failed to notify user service", sl.Err(err))

			render.JSON(w, r, resp.Error(
				map[string][]string{"userService": {"Failed to notify user service"}},
				http.StatusInternalServerError,
			))
			return
		}
		if !grpcDeleteResponse.Success {
			log.Error("user service returned failure", slog.String("message", grpcDeleteResponse.Message))

			render.JSON(w, r, resp.Error(
				map[string][]string{"userService": {"User service returned failure: " + grpcDeleteResponse.Message}},
				http.StatusInternalServerError,
			))
			return
		}

		log.Info("post succesfully removed from user")

		err = postRemover.Delete(
			context.TODO(),
			postId,
		)
		if err != nil {
			log.Error("can not delete post", sl.Err(err))

			log.Info("rolling back post")
			post, getByIdErr := postProvider.GetById(context.TODO(), postId)
			if getByIdErr != nil {
				log.Error("failed to retrieve post for rollback", sl.Err(getByIdErr))

				render.JSON(w, r, resp.Error(map[string][]string{
					"post": {"Failed to retrieve post for rollback: " + getByIdErr.Error()},
				}, http.StatusInternalServerError))

				return
			}

			_, rollbackErr := grpcClient.RestoreUserPost(context.TODO(), &pb.RestorePostRequest{
				UserId: post.User.Hex(),
				PostId: postId.Hex(),
			})
			if rollbackErr != nil {
				log.Error("failed to rollback post in user service", sl.Err(rollbackErr))

				render.JSON(w, r, resp.Error(map[string][]string{
					"rollback": {"Failed to rollback post in user service: " + rollbackErr.Error()},
				}, http.StatusInternalServerError))

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
