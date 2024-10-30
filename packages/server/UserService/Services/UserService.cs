using Grpc.Core;
using MongoDB.Driver;
using UserService.Models.User;

namespace UserService.Services;

public class UserService : global::UserService.UserService.UserServiceBase
{
   private readonly ILogger<UserService> _logger;
   private readonly IMongoCollection<User> _userCollection;
   
   public UserService(ILogger<UserService> logger, IMongoCollection<User> userCollection)
   {
      _logger = logger;
      _userCollection = userCollection;
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
}