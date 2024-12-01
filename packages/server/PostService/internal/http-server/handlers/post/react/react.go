package react

import (
	"context"
	"encoding/json"
	"fmt"
	"log/slog"
	"net/http"

	pb "github.com/ShutSasha/devhub/tree/main/packages/server/PostService/gen/go/user"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/domain/interfaces"
	"github.com/ShutSasha/devhub/tree/main/packages/server/PostService/internal/http-server/utils"
	"github.com/go-playground/validator/v10"

	"github.com/go-chi/chi/v5"
	"github.com/go-chi/render"
	"github.com/google/uuid"
	"go.mongodb.org/mongo-driver/bson/primitive"
)

// Request struct defines the JSON request body for the handler.
// - UserId: The ID of the user creating the post (required).
type ReactionRequest struct {
	UserId string `json:"userId" validate:"required"`
}

// NewLike adds a like to a post.
// @Summary Add a like to a post
// @Description Adds a like to the specified post on behalf of a user.
// @Tags Reactions
// @Accept json
// @Produce json
// @Param id path string true "Post ID"
// @Param body body ReactionRequest true "Request data"
// @Success 200 {object} map[string]interface{}
// @Failure 400 {object} map[string]interface{}
// @Failure 500 {object} map[string]interface{}
// @Router /api/posts/{id}/like [post]
func NewLike(
	log *slog.Logger,
	postReactor interfaces.PostReactor,
	postProvider interfaces.PostProvider,
	fileProvider interfaces.FileProvider,
	grpcClient pb.UserServiceClient,
) http.HandlerFunc {
	return handleReaction(log, postReactor, postProvider, fileProvider, grpcClient, "like")
}

// NewDislike adds a dislike to a post.
// @Summary Add a dislike to a post
// @Description Adds a dislike to the specified post on behalf of a user.
// @Tags Reactions
// @Accept json
// @Produce json
// @Param id path string true "Post ID"
// @Param body body ReactionRequest true "Request data"
// @Success 200 {object} map[string]interface{}
// @Failure 400 {object} map[string]interface{}
// @Failure 500 {object} map[string]interface{}
// @Router /api/posts/{id}/dislike [post]
func NewDislike(
	log *slog.Logger,
	postReactor interfaces.PostReactor,
	postProvider interfaces.PostProvider,
	fileProvider interfaces.FileProvider,
	grpcClient pb.UserServiceClient,
) http.HandlerFunc {
	return handleReaction(log, postReactor, postProvider, fileProvider, grpcClient, "dislike")
}

func handleReaction(
	log *slog.Logger,
	postReactor interfaces.PostReactor,
	postProvider interfaces.PostProvider,
	fileProvider interfaces.FileProvider,
	grpcClient pb.UserServiceClient,
	reactionType string,
) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.react.handleReaction"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", uuid.NewString()),
		)

		postId := chi.URLParam(r, "id")

		postObjectId, err := primitive.ObjectIDFromHex(postId)
		if err != nil {
			utils.HandleError(log, w, r, "failed to create objectId from request", err, http.StatusBadRequest, "postId", "Invalid postId format")
			return
		}

		var request ReactionRequest
		if err = json.NewDecoder(r.Body).Decode(&request); err != nil {
			utils.HandleError(log, w, r, "failed to decode from request", err, http.StatusBadRequest, "postId", "failed to decode from request")
			return
		}

		if request.UserId == "" {
			utils.HandleError(log, w, r, "wrong user id", fmt.Errorf("wrong user id"), http.StatusUnauthorized, "user", "user unauthorized")
			return
		}

		if err := validator.New().Struct(request); err != nil {
			validateErr := err.(validator.ValidationErrors)
			utils.HandleValidatorError(log, w, r, "invalid request", err, validateErr, http.StatusBadRequest)
			return
		}

		_, err = primitive.ObjectIDFromHex(request.UserId)
		if err != nil {
			utils.HandleError(log, w, r, "failed to create objectId from request", err, http.StatusBadRequest, "postId", "Invalid userId format")
			return
		}

		resp, err := grpcClient.AddPostReactionToUser(context.TODO(), &pb.AddReactionRequest{
			UserId: request.UserId,
			PostId: postId,
			Type:   reactionType,
		})
		if err != nil || (resp != nil && !resp.Success) {
			{
				message := "failed to add reaction to user"
				utils.HandleError(log, w, r, message,
					fmt.Errorf("%s: %s", op, resp.Message), http.StatusInternalServerError, reactionType, message)
				return
			}
		}

		if err = postReactor.React(context.TODO(), postObjectId, int(resp.Likes), int(resp.Dislikes)); err != nil {
			message := "failed to react on post"
			utils.HandleError(log, w, r, message, err, http.StatusBadRequest, "post", message)
			return
		}

		post, err := postProvider.GetById(context.TODO(), postObjectId, fileProvider)
		if err != nil {
			utils.HandleError(log, w, r, "failed to get post with provided post id", err, http.StatusBadRequest, "postId", "Error providing post")
			return
		}

		log.Info(fmt.Sprintf("%s reaction added successfully", reactionType))
		render.JSON(w, r, post)
	}
}
