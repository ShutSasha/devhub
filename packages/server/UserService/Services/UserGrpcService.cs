using Grpc.Core;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using MongoDB.Driver.Core.Events;
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
   
   public override async Task<AddPostResponse> AddPostToUser(AddPostRequest request, ServerCallContext context)
   {
      var userId = request.UserId;

      var filter = Builders<User>.Filter.Eq(u => u.Id, userId);
      var update = Builders<User>.Update.Push(u => u.Posts, request.PostId);

      var result = await _userCollection.UpdateOneAsync(filter, update);
      

      return result.ModifiedCount > 0
         ? new AddPostResponse { Success = true, Message = "Post added successfully" }
         : new AddPostResponse { Success = false, Message = "Failed to add post" };
   }

   public override async Task<DeletePostResponse> DeletePostFromUser(DeletePostRequest request,
      ServerCallContext context)
   {
      var filter = Builders<User>.Filter.AnyEq(u => u.Posts, request.PostId);
      var user = await _userCollection.Find(filter).FirstOrDefaultAsync();

      if (user == null)
      {
         return new DeletePostResponse
         {
            Success = false,
            Message = "User or post not found."
         };
      }
      
      _logger.LogInformation($"User with id: {user.Id} was successfully found");
      
      var update = Builders<User>.Update.Pull(u => u.Posts, request.PostId);
      var updateResult = await _userCollection.UpdateOneAsync(filter, update);

      return updateResult.ModifiedCount > 0
         ? new DeletePostResponse { Success = true, Message = "Post deleted successfully." }
         : new DeletePostResponse { Success = false, Message = "Failed to delete post" };
   }

   public override async Task<RestorePostResponse> RestoreUserPost(RestorePostRequest request,
      ServerCallContext context)
   {
      var user = await _userCollection.Find(u => u.Id == request.UserId).FirstOrDefaultAsync();

      if (user == null)
      {
         return new RestorePostResponse
         {
            Success = false,
            Message = "User wasn't found"
         };
      }

      if (user.Posts.Contains(request.PostId))
      {
         return new RestorePostResponse
         {
            Success = false,
            Message = "Post is already restored"
         };
      }
      _logger.LogInformation($"User with id: {user.Id} was successfully found");
      var update = Builders<User>.Update.Push(u => u.Posts, request.PostId);
      var result = await _userCollection.UpdateOneAsync(u => u.Id == request.UserId, update);
      
      return result.ModifiedCount > 0 
         ? new RestorePostResponse { Success = true, Message = "Post restored successfully" }
         : new RestorePostResponse { Success = false, Message = "Failed to restore post" };
   }

   public override async Task<AddCommentResponse> AddCommentToUser(AddCommentRequest request, ServerCallContext context)
   {
      var userId = request.Id;

      _logger.LogInformation("retrieved information");
      
      var candidate = Builders<User>.Filter.Eq(u => u.Id, userId);
      
      if (candidate == null)
      {
         return new AddCommentResponse
         {
            Success = false,
            Message =$"User with id: {request.Id} wasn't found "
         };
      }

      var userUpdate = Builders<User>.Update.Push(u=> u.Comments,request.CommentId);
      var updateResult = await _userCollection.UpdateOneAsync(candidate, userUpdate);
      
      return updateResult.ModifiedCount > 0 
         ? new AddCommentResponse { Success = true, Message = "Successfully added comment" }
         : new AddCommentResponse { Success = false, Message = "Can't update user model" };
   }

   public override async Task<DeleteCommentResponse> DeleteCommentFromUser(DeleteCommentRequest request, ServerCallContext context)
   {
      var filter = Builders<User>.Filter.AnyEq(u => u.Posts, request.CommentId);
      var user = await _userCollection.Find(filter).FirstOrDefaultAsync();
      
      if (user == null)
      {
         return new DeleteCommentResponse
         {
            Success = false,
            Message ="User wasn't found "
         };
      }
      
      var update = Builders<User>.Update.Pull(u => u.Comments, request.CommentId);
      var updateResult = await _userCollection.UpdateOneAsync(filter, update);

      return updateResult.ModifiedCount > 0
         ? new DeleteCommentResponse { Success = true, Message = "Successfully delete comment" }
         : new DeleteCommentResponse { Success = false, Message = "Can't delete comment from user" };
   }
   
}