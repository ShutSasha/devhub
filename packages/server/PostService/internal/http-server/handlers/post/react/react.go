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
	grpcClient pb.UserServiceClient,
) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.react.NewLike"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", uuid.NewString()),
		)

		id := chi.URLParam(r, "id")

		oid, err := primitive.ObjectIDFromHex(id)
		if err != nil {
			utils.HandleError(log, w, r, "failed to create objectId from request", err, http.StatusBadRequest, "postId", "Invalid postId format")
			return
		}

		var request ReactionRequest
		if err = json.NewDecoder(r.Body).Decode(&request); err != nil {
			utils.HandleError(log, w, r, "failed to decode from request", err, http.StatusBadRequest, "postId", "failed to decode from request")
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
			PostId: id,
			Type:   "like",
		})
		if resp != nil {
			if !resp.Success {
				utils.HandleError(log, w, r, "failed to add like from user",
					fmt.Errorf("%s: %s", op, resp.Message), http.StatusInternalServerError, "like", "failed to add like from user")
				return
			}
		}
		if err != nil {
			utils.HandleError(log, w, r, "failed to remove like from user",
				err, http.StatusInternalServerError, "like", "failed to add like from user")
			return
		}

		if err = postReactor.AddLike(context.TODO(), oid); err != nil {
			utils.HandleError(log, w, r, "failed to like post", err, http.StatusBadRequest, "post", "failed to like post")
			return
		}

		log.Info("liked successfully")
		render.JSON(w, r, map[string]string{"status": "success"})
	}
}

// NewUnlike removes a like from a post.
// @Summary Remove a like from a post
// @Description Removes a like from the specified post on behalf of a user.
// @Tags Reactions
// @Accept json
// @Produce json
// @Param id path string true "Post ID"
// @Param body body ReactionRequest true "Request data"
// @Success 200 {object} map[string]interface{}
// @Failure 400 {object} map[string]interface{}
// @Failure 500 {object} map[string]interface{}
// @Router /api/posts/{id}/like [delete]
func NewUnlike(
	log *slog.Logger,
	postReactor interfaces.PostReactor,
	grpcClient pb.UserServiceClient,
) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.react.NewUnlike"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", uuid.NewString()),
		)

		id := chi.URLParam(r, "id")

		oid, err := primitive.ObjectIDFromHex(id)
		if err != nil {
			utils.HandleError(log, w, r, "failed to create objectId from request", err, http.StatusBadRequest, "postId", "Invalid postId format")
			return
		}

		var request ReactionRequest
		if err = json.NewDecoder(r.Body).Decode(&request); err != nil {
			utils.HandleError(log, w, r, "failed to decode from request", err, http.StatusBadRequest, "postId", "failed to decode from request")
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

		resp, err := grpcClient.DeletePostReactionFromUser(context.TODO(), &pb.DeleteReactionRequest{
			UserId: request.UserId,
			PostId: id,
			Type:   "like",
		})
		if resp != nil {
			if !resp.Success {
				utils.HandleError(log, w, r, "failed to remove like from user",
					fmt.Errorf("%s: %s", op, resp.Message), http.StatusInternalServerError, "like", "failed to remove like from user")
				return
			}
		}
		if err != nil {
			utils.HandleError(log, w, r, "failed to remove like from user",
				err, http.StatusInternalServerError, "like", "failed to remove like from user")
			return
		}

		if err = postReactor.RemoveLike(context.TODO(), oid); err != nil {
			utils.HandleError(log, w, r, "failed to unlike post", err, http.StatusBadRequest, "post", "failed to unlike post")
			return
		}

		log.Info("unliked successfully")
		render.JSON(w, r, map[string]string{"status": "success"})
	}
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
	grpcClient pb.UserServiceClient,
) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.react.NewDislike"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", uuid.NewString()),
		)

		id := chi.URLParam(r, "id")

		oid, err := primitive.ObjectIDFromHex(id)
		if err != nil {
			utils.HandleError(log, w, r, "failed to create objectId from request", err, http.StatusBadRequest, "postId", "Invalid postId format")
			return
		}

		var request ReactionRequest
		if err = json.NewDecoder(r.Body).Decode(&request); err != nil {
			utils.HandleError(log, w, r, "failed to decode from request", err, http.StatusBadRequest, "postId", "failed to decode from request")
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
			PostId: id,
			Type:   "dislike",
		})
		if resp != nil {
			if !resp.Success {
				utils.HandleError(log, w, r, "failed to add dislike from user",
					fmt.Errorf("%s: %s", op, resp.Message), http.StatusInternalServerError, "dislike", "failed to add dislike from user")
				return
			}
		}
		if err != nil {
			utils.HandleError(log, w, r, "failed to remove dislike from user",
				err, http.StatusInternalServerError, "dislike", "failed to add dislike from user")
			return
		}

		if err = postReactor.AddDislike(context.TODO(), oid); err != nil {
			utils.HandleError(log, w, r, "failed to dislike post", err, http.StatusBadRequest, "post", "failed to dislike post")
			return
		}

		log.Info("disliked successfully")
		render.JSON(w, r, map[string]string{"status": "success"})
	}
}

// NewUndislike removes a dislike from a post.
// @Summary Remove a dislike from a post
// @Description Removes a dislike from the specified post on behalf of a user.
// @Tags Reactions
// @Accept json
// @Produce json
// @Param id path string true "Post ID"
// @Param body body ReactionRequest true "Request data"
// @Success 200 {object} map[string]interface{}
// @Failure 400 {object} map[string]interface{}
// @Failure 500 {object} map[string]interface{}
// @Router /api/posts/{id}/dislike [delete]
func NewUndislike(
	log *slog.Logger,
	postReactor interfaces.PostReactor,
	grpcClient pb.UserServiceClient,
) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		const op = "handlers.post.react.NewUndislike"

		log := log.With(
			slog.String("op", op),
			slog.String("request_id", uuid.NewString()),
		)

		id := chi.URLParam(r, "id")

		oid, err := primitive.ObjectIDFromHex(id)
		if err != nil {
			utils.HandleError(log, w, r, "failed to create objectId from request", err, http.StatusBadRequest, "postId", "Invalid postId format")
			return
		}

		var request ReactionRequest
		if err = json.NewDecoder(r.Body).Decode(&request); err != nil {
			utils.HandleError(log, w, r, "failed to decode from request", err, http.StatusBadRequest, "postId", "failed to decode from request")
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

		resp, err := grpcClient.DeletePostReactionFromUser(context.TODO(), &pb.DeleteReactionRequest{
			UserId: request.UserId,
			PostId: id,
			Type:   "dislike",
		})
		if resp != nil {
			if !resp.Success {
				utils.HandleError(log, w, r, "failed to remove dislike from user",
					fmt.Errorf("%s: %s", op, resp.Message), http.StatusInternalServerError, "dislike", "failed to remove dislike from user")
				return
			}
		}
		if err != nil {
			utils.HandleError(log, w, r, "failed to remove dislike from user",
				err, http.StatusInternalServerError, "dislike", "failed to remove dislike from user")
			return
		}

		if err = postReactor.RemoveDislike(context.TODO(), oid); err != nil {
			utils.HandleError(log, w, r, "failed to undislike post", err, http.StatusBadRequest, "post", "failed to undislike post")
			return
		}

		log.Info("undisliked successfully")
		render.JSON(w, r, map[string]string{"status": "success"})
	}
}
