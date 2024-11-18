using Grpc.Core;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using MongoDB.Driver.Core.Events;
using MongoDB.Driver.Linq;
using UserService.Models.Database;
using UserService.Models.User;

namespace UserService.Services;

public class UserGrpcService : global::UserService.UserService.UserServiceBase
{
   private readonly ILogger<UserGrpcService> _logger;
   private readonly IMongoCollection<User> _userCollection;

   public UserGrpcService(IMongoDatabase mongoDatabase, IOptions<MongoDbSettings> mongoDbSettings,
      ILogger<UserGrpcService> logger)
   {
      _logger = logger;
      _userCollection = mongoDatabase.GetCollection<User>(mongoDbSettings.Value.CollectionName);
   }

   public override async Task<UserResponse> AddPostToUser(AddPostRequest request, ServerCallContext context)
   {
      var userId = request.UserId;

      var filter = Builders<User>.Filter.Eq(u => u.Id, userId);
      var update = Builders<User>.Update.Push(u => u.Posts, request.PostId);

      var result = await _userCollection.UpdateOneAsync(filter, update);


      return result.ModifiedCount > 0
         ? new UserResponse { Success = true, Message = "Post added successfully" }
         : new UserResponse { Success = false, Message = "Failed to add post" };
   }

   public override async Task<UserResponse> DeletePostFromUser(DeletePostRequest request,
      ServerCallContext context)
   {
      var filter = Builders<User>.Filter.AnyEq(u => u.Posts, request.PostId);
      var user = await _userCollection.Find(filter).FirstOrDefaultAsync();

      if (user == null)
      {
         return new UserResponse
         {
            Success = false,
            Message = "User or post not found."
         };
      }

      _logger.LogInformation($"User with id: {user.Id} was successfully found");

      var update = Builders<User>.Update.Pull(u => u.Posts, request.PostId);
      var updateResult = await _userCollection.UpdateOneAsync(filter, update);

      return updateResult.ModifiedCount > 0
         ? new UserResponse { Success = true, Message = "Post deleted successfully." }
         : new UserResponse { Success = false, Message = "Failed to delete post" };
   }

   public override async Task<UserResponse> RestoreUserPost(RestorePostRequest request,
      ServerCallContext context)
   {
      var user = await _userCollection.Find(u => u.Id == request.UserId).FirstOrDefaultAsync();

      if (user == null)
      {
         return new UserResponse
         {
            Success = false,
            Message = "User wasn't found"
         };
      }

      if (user.Posts.Contains(request.PostId))
      {
         return new UserResponse
         {
            Success = false,
            Message = "Post is already restored"
         };
      }

      _logger.LogInformation($"User with id: {user.Id} was successfully found");
      var update = Builders<User>.Update.Push(u => u.Posts, request.PostId);
      var result = await _userCollection.UpdateOneAsync(u => u.Id == request.UserId, update);

      return result.ModifiedCount > 0
         ? new UserResponse { Success = true, Message = "Post restored successfully" }
         : new UserResponse { Success = false, Message = "Failed to restore post" };
   }

   public override async Task<UserResponse> AddCommentToUser(AddCommentRequest request, ServerCallContext context)
   {
      var userId = request.Id;

      var candidate = Builders<User>.Filter.Eq(u => u.Id, userId);

      if (candidate == null)
      {
         return new UserResponse
         {
            Success = false,
            Message = $"User with id: {request.Id} wasn't found "
         };
      }

      _logger.LogInformation($"Preparing for updating user with id {userId}");
      var userUpdate = Builders<User>.Update.Push(u => u.Comments, request.CommentId);
      var updateResult = await _userCollection.UpdateOneAsync(candidate, userUpdate);

      return updateResult.ModifiedCount > 0
         ? new UserResponse { Success = true, Message = "Successfully added comment" }
         : new UserResponse { Success = false, Message = "Can't update user model" };
   }

   public override async Task<UserResponse> DeleteCommentFromUser(DeleteCommentRequest request,
      ServerCallContext context)
   {
      var user = await _userCollection.Find(u => u.Id == request.UserId)
         .FirstOrDefaultAsync();

      if (user == null)
      {
         return new UserResponse
         {
            Success = false,
            Message = "User wasn't found "
         };
      }

      _logger.LogInformation($"Preparing for updating user with id {user.Id}");
      var filter = Builders<User>.Filter.Eq(u => u.Id, request.UserId);
      var update = Builders<User>.Update.Pull(u => u.Comments, request.CommentId);
      var updateResult = await _userCollection.UpdateOneAsync(filter, update);

      return updateResult.ModifiedCount > 0
         ? new UserResponse { Success = true, Message = "Successfully delete comment" }
         : new UserResponse { Success = false, Message = "Can't delete comment from user" };
   }

   public override async Task<AddReactionResponse> AddPostReactionToUser(AddReactionRequest request,
      ServerCallContext context)
   {
      var reactionResponse = new AddReactionResponse
      {
         Dislikes = 0,
         Likes = 0,
         Message = string.Empty,
         Success = false
      };

      var user = await _userCollection.Find(u => u.Id == request.UserId).FirstOrDefaultAsync();
      if (user == null)
      {
         reactionResponse.Message = "User wasn't found";
         return reactionResponse;
      }

      var hasLikeOnPost = user.LikedPosts.Contains(request.PostId);
      var hasDislikeOnPost = user.DislikedPosts.Contains(request.PostId);

      var filter = Builders<User>.Filter.Eq(u => u.Id, request.UserId);
      UpdateDefinition<User>? update = null;

      (string type, bool hasLike, bool hasDislike) = (request.Type.ToLower(), hasLikeOnPost, hasDislikeOnPost);

      switch (type, hasLike, hasDislike)
      {
         case ("like", false, false):
            reactionResponse.Likes = 1;
            update = Builders<User>.Update.Push(u => u.LikedPosts, request.PostId);
            break;

         case ("dislike", false, false):
            reactionResponse.Dislikes = 1;
            update = Builders<User>.Update.Push(u => u.DislikedPosts, request.PostId);
            break;

         case ("like", true, false):
            reactionResponse.Likes = -1;
            update = Builders<User>.Update.Pull(u => u.LikedPosts, request.PostId);
            break;

         case ("dislike", false, true):
            reactionResponse.Dislikes = -1;
            update = Builders<User>.Update.Pull(u => u.DislikedPosts, request.PostId);
            break;

         case ("like", false, true):
            reactionResponse.Dislikes = -1;
            reactionResponse.Likes = 1;
            update = Builders<User>.Update
               .Pull(u => u.DislikedPosts, request.PostId)
               .AddToSet(u => u.LikedPosts, request.PostId);
            break;

         case ("dislike", true, false):
            reactionResponse.Likes = -1;
            reactionResponse.Dislikes = 1;
            update = Builders<User>.Update
               .Pull(u => u.LikedPosts, request.PostId)
               .AddToSet(u => u.DislikedPosts, request.PostId);
            break;

         default:
            reactionResponse.Message = $"Invalid reaction type: {request.Type}";
            _logger.LogWarning(reactionResponse.Message);
            return reactionResponse;
      }

      if (update == null)
      {
         reactionResponse.Message = "No changes to apply.";
         _logger.LogInformation(reactionResponse.Message);
         return reactionResponse;
      }

      _logger.LogInformation($"Updating user {user.Id} with reaction {type} on post {request.PostId}");
      var updateResult = await _userCollection.UpdateOneAsync(filter, update);

      if (updateResult.ModifiedCount > 0)
      {
         reactionResponse.Success = true;
         reactionResponse.Message = "Reaction updated successfully";
      }
      else
      {
         reactionResponse.Message = "Failed to update reaction";
         _logger.LogWarning($"No documents were modified for user {user.Id}");
      }

      return reactionResponse;
   }

   public override async Task<UserResponse> DeleteReactedPost(DeleteReactedPostRequest request,
      ServerCallContext context)
   {
      if (string.IsNullOrWhiteSpace(request.PostId))
      {
         return new UserResponse
         {
            Success = false,
            Message = "Post ID is required."
         };
      }

      var postId = request.PostId;

      var filter = Builders<User>.Filter.Or(
         Builders<User>.Filter.AnyEq(u => u.LikedPosts, postId),
         Builders<User>.Filter.AnyEq(u => u.DislikedPosts, postId)
      );

      var update = Builders<User>.Update
         .Pull(u => u.LikedPosts, postId)
         .Pull(u => u.DislikedPosts, postId);

      try
      {
         var updateResult = await _userCollection.UpdateManyAsync(filter, update);

         if (updateResult.ModifiedCount > 0)
         {
            _logger.LogInformation($"Post {postId} removed from {updateResult.ModifiedCount} user(s).");

            return new UserResponse
            {
               Success = true,
               Message = $"Post {postId} reactions removed successfully."
            };
         }

         _logger.LogWarning($"Post {postId} was not found in any user's reactions.");
         return new UserResponse
         {
            Success = true,
            Message = "No users had reacted to the post."
         };
      }
      catch (Exception ex)
      {
         _logger.LogError($"Failed to remove reactions for post {postId}: {ex.Message}");
         return new UserResponse
         {
            Success = false,
            Message = "An error occurred while removing reactions."
         };
      }
   }
}