using Grpc.Core;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using UserService.Models.Database;
using UserService.Models.User;

namespace UserService.Services;

public class UserService : global::UserService.UserService.UserServiceBase
{
   private readonly ILogger<UserService> _logger;
   private readonly IMongoCollection<User> _userCollection;

   public UserService(IMongoDatabase mongoDatabase, IOptions<MongoDbSettings> mongoDbSettings,
      ILogger<UserService> logger)
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


      if (result.ModifiedCount > 0)
      {
         return new AddPostResponse { Success = true, Message = "Post added successfully" };
      }
      else
      {
         return new AddPostResponse { Success = false, Message = "Failed to add post" };
      }
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

      var update = Builders<User>.Update.Pull(u => u.Posts, request.PostId);
      var updateResult = await _userCollection.UpdateOneAsync(filter, update);

      if (updateResult.ModifiedCount > 0)
      {
         return new DeletePostResponse
         {
            Success = true,
            Message = "Post deleted successfully."
         };
      }

      return new DeletePostResponse
      {
         Success = false,
         Message = "Failed to delete post."
      };
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

      var update = Builders<User>.Update.Push(u => u.Posts, request.PostId);
      var result = await _userCollection.UpdateOneAsync(u => u.Id == request.UserId, update);

      if (result.ModifiedCount > 0)
      {
         return new RestorePostResponse
         {
            Success = true,
            Message = "Post restored successfully"
         };
      }

      return new RestorePostResponse
      {
         Success = false,
         Message = "Failed to restore post"
      };
   }
}