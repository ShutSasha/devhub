using AutoMapper;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using Post;
using UserService.Abstracts;
using UserService.Contracts.User;
using UserService.Dto;
using UserService.Models.Database;
using UserService.Models.User;

namespace UserService.Services;

public class UserService : IUserService
{
   private readonly ILogger<UserService> _logger;
   private readonly IStorageService _storageService;
   private readonly IMapper _mapper;
   private readonly PostService.PostServiceClient _postServiceClient;
   private readonly IMongoCollection<User> _userCollection;
   private readonly IMongoCollection<Models.User.Post> _postCollection;
   private readonly IMongoCollection<Comment> _commentCollection;


   public UserService(IMongoDatabase mongoDatabase, IOptions<MongoDbSettings> mongoDbSettings,
      ILogger<UserService> logger, IStorageService storageService, IMapper mapper,
      PostService.PostServiceClient postServiceClient)
   {
      _logger = logger;
      _storageService = storageService;
      _mapper = mapper;
      _postServiceClient = postServiceClient;
      _commentCollection = mongoDatabase.GetCollection<Comment>("comments");
      _postCollection = mongoDatabase.GetCollection<Models.User.Post>("posts");
      _userCollection = mongoDatabase.GetCollection<User>(mongoDbSettings.Value.CollectionName);
   }

   public async Task<UserDto> EditUser(string id, string name, string bio)
   {
      var filter = Builders<User>.Filter.Eq(u => u.Id, id);
      var user = await _userCollection.Find(filter).FirstOrDefaultAsync();

      if (user == null)
      {
         _logger.LogError($"User with ID {id} not found.");
         throw new Exception($"404:User wasn't found.");
      }

      var update = Builders<User>.Update
         .Set(u => u.Name, name)
         .Set(u => u.Bio, bio);

      var result = await _userCollection.UpdateOneAsync(filter, update);

      if (result.ModifiedCount == 0)
      {
         _logger.LogWarning($"No changes were made to user with ID {id}.");
         throw new Exception($"400:No changes were made.");
      }

      var updatedUser = await _userCollection.Find(filter).FirstOrDefaultAsync();
      _logger.LogInformation($"User with ID {id} updated successfully.");
      return _mapper.Map<User, UserDto>(updatedUser);
   }

   public async Task<string> EditUserIcon(string id, string fileName, Stream fileStream, string contentType)
   {
      var candidate = await _userCollection.Find(u => u.Id == id)
         .FirstOrDefaultAsync();

      if (candidate == null)
      {
         _logger.LogError($"404: User with this {id} not found");
         throw new Exception($"404: User with this {id} not found");
      }

      var updatePictureResult = await _storageService.UploadFileAsync(id, fileName, fileStream, contentType);

      if (candidate.Avatar.StartsWith("https://mydevhubimagebucket"))
      {
         await _storageService.DeleteFileAsync(candidate.Avatar);
      }

      candidate.Avatar = updatePictureResult;

      var filter = Builders<User>.Filter.Eq(u => u.Id, id);
      var update = Builders<User>.Update.Set(u => u.Avatar, candidate.Avatar);

      var result = await _userCollection.UpdateOneAsync(filter, update);

      if (result.ModifiedCount > 0)
      {
         _logger.LogInformation($"User {id} avatar updated successfully.");
         return candidate.Avatar;
      }
      else
      {
         _logger.LogError($"Failed to update avatar for user {id}.");
         throw new Exception($"500: Failed to update avatar for user {id}.");
      }
   }

   public async Task<User> GetById(string id)
   {
      var candidate = await _userCollection.Find(u => u.Id == id).FirstOrDefaultAsync();
      if (candidate != null)
      {
         return candidate;
      }

      throw new Exception("404: User with this id wasn't found");
   }

   public async Task<UserReactionsResponse> GetUserReaction(string userId)
   {
      var candidate = await GetById(userId);
      return new UserReactionsResponse
      {
         DislikedPosts = candidate.DislikedPosts,
         LikedPosts = candidate.LikedPosts,
      };
   }

   public async Task<UserDto> AddUserFollowing(string userId, string followingId)
   {
      var (user, _) = await GetUsersForFollowingAction(userId, followingId);

      if (user.Followings.Contains(followingId))
         throw new Exception($"400: You've already subscribed to this user");

      var userUpdate = Builders<User>.Update.AddToSet(u => u.Followings, followingId);
      var targetUserUpdate = Builders<User>.Update.AddToSet(u => u.Followers, userId);

      _logger.LogInformation($"Preparing to update users with id {userId}, {followingId}");

      var userUpdateTask = _userCollection.UpdateOneAsync(
         Builders<User>.Filter.Eq(u => u.Id, userId),
         userUpdate);

      var targetUserUpdateTask = _userCollection.UpdateOneAsync(
         Builders<User>.Filter.Eq(u => u.Id, followingId),
         targetUserUpdate);

      await Task.WhenAll(userUpdateTask, targetUserUpdateTask);

      if (userUpdateTask.Result.ModifiedCount == 0 || targetUserUpdateTask.Result.ModifiedCount == 0)
         throw new Exception("500: Failed to update user or target user data");

      user.Followings.Add(followingId);
      return _mapper.Map<User, UserDto>(user);
   }

   private async Task<(User User, User TargetUser)> GetUsersForFollowingAction(string userId, string followingId)
   {
      var users = await _userCollection.Find(u => u.Id == userId || u.Id == followingId).ToListAsync();

      var user = users.FirstOrDefault(u => u.Id == userId);
      var targetUser = users.FirstOrDefault(u => u.Id == followingId);

      if (user == null || targetUser == null)
         throw new Exception("404: User or target user wasn't found");

      _logger.LogInformation($"users with id {userId}, {followingId} were found");
      return (user, targetUser);
   }

   public async Task<UserDto> RemoveUserFollowing(string userId, string followingId)
   {
      var (user, targetUser) = await GetUsersForFollowingAction(userId, followingId);

      if (!user.Followings.Contains(followingId))
         throw new Exception("400: You're not subscribed to this user");

      var userUpdate = Builders<User>.Update.Pull(u => u.Followings, followingId);
      var targetUserUpdate = Builders<User>.Update.Pull(u => u.Followers, userId);

      _logger.LogInformation($"Preparing to update users with id {userId}, {followingId}");
      var userUpdateTask = _userCollection.UpdateOneAsync(
         Builders<User>.Filter.Eq(u => u.Id, userId),
         userUpdate);

      var targetUserUpdateTask = _userCollection.UpdateOneAsync(
         Builders<User>.Filter.Eq(u => u.Id, followingId),
         targetUserUpdate);

      await Task.WhenAll(userUpdateTask, targetUserUpdateTask);

      if (userUpdateTask.Result.ModifiedCount == 0 || targetUserUpdateTask.Result.ModifiedCount == 0)
         throw new Exception("500: Failed to update user or target user data");

      user.Followings.Remove(followingId);
      targetUser.Followers.Remove(userId);

      _logger.LogInformation($"users with id {userId}, {followingId} updated successfully");
      return _mapper.Map<User, UserDto>(user);
   }

   public async Task<List<UserConnectionsDto>> GetUserConnections(string userId, string connectionType)
   {
      var user = await GetById(userId);

      var ids = connectionType.ToLower() switch
      {
         "followings" => user.Followings,
         "followers" => user.Followers,
         _ => throw new Exception("400: Invalid connection type")
      };

      var users = await _userCollection
         .Find(u => ids.Contains(u.Id))
         .ToListAsync();

      var connections = _mapper.Map<List<UserConnectionsDto>>(users);

      return connections;
   }

   public async Task<bool> CheckUserFollowing(string userId, string targetUserId)
   {
      var (user, targetUser) = await GetUsersForFollowingAction(userId, targetUserId);

      return user.Followings.Contains(targetUserId);
   }

   public async Task UpdateSavedPost(string userId, string savedPostId, bool isAdding)
   {
      var user = await GetById(userId);
      if (user == null)
         throw new Exception("404: User not found");

      if (user.SavedPosts.Contains(savedPostId) == isAdding)
      {
         var action = isAdding ? "saved" : "deleted";
         throw new Exception($"400: You've already {action} this post");
      }

      UpdateDefinition<User>? updateDefinition = null;

      Post.UserResponse? repsonse = null;
      switch (isAdding)
      {
         case true:
            updateDefinition = Builders<User>.Update.AddToSet(u => u.SavedPosts, savedPostId);
            repsonse = _postServiceClient.UpdateSavedPost(new Post.UpdateSavedPostRequest
            {
               PostId = savedPostId,
               Value = 1,
            });
            break;
         case false:
            updateDefinition = Builders<User>.Update.Pull(u => u.SavedPosts, savedPostId);
            repsonse = _postServiceClient.UpdateSavedPost(new Post.UpdateSavedPostRequest
            {
               PostId = savedPostId,
               Value = -1,
            });
            break;
      }

      if (!repsonse.Success)
      {
         throw new Exception($"500: {repsonse.Message}");
      }

      var userUpdateResult = await _userCollection.UpdateOneAsync(
         Builders<User>.Filter.Eq(u => u.Id, userId),
         updateDefinition);

      if (userUpdateResult.ModifiedCount <= 0)
      {
         throw new Exception("500: Can't update user");
      }
   }

   public async Task<List<string>> GetSavedPosts(string userId)
   {
      var user = await GetById(userId);

      return user.SavedPosts;
   }

   public async Task<List<PostDto>> GetDetailedSavedPosts(string userId)
   {
      var user = await GetById(userId);

      var savedPostIds = user.SavedPosts;
      if (!savedPostIds.Any())
      {
         return new List<PostDto>();
      }

      var postsWithAuthors = await _postCollection
         .Find(p => savedPostIds.Contains(p.Id))
         .ToListAsync();

      var result = new List<PostDto>();

      foreach (var post in postsWithAuthors)
      {
         var author = await _userCollection
            .Find(u => u.Id == post.UserId.ToString())
            .Project(u => new SavedPostUserDto
            {
               Id = u.Id,
               Name = u.Name,
               Username = u.UserName,
               Avatar = u.Avatar,
               DevPoints = u.DevPoints
            })
            .FirstOrDefaultAsync();

         if (author == null)
         {
            throw new Exception($"500: Author not found for post {post.Id}");
         }

         var detailedPost = new PostDto
         {
            Id = post.Id,
            User = author,
            Title = post.Title,
            Content = post.Content,
            CreatedAt = post.CreatedAt,
            Likes = post.Likes,
            Dislikes = post.Dislikes,
            HeaderImage = post.HeaderImage,
            Comments = post.Comments,
            Tags = post.Tags
         };

         result.Add(detailedPost);
      }

      return result;
   }

   public async Task<UserDetailsResponse> GetUserDetailsById(string id)
   {
      var user = await GetById(id);

      var posts = await _postCollection
         .Find(p => user.Posts.Contains(p.Id))
         .Project(p => new Models.User.Post
         {
            Id = p.Id,
            Title = p.Title,
            Content = p.Content,
            HeaderImage = p.HeaderImage,
            Likes = p.Likes,
            Dislikes = p.Dislikes,
            Tags = p.Tags,
            CreatedAt = p.CreatedAt
         })
         .ToListAsync();

      var comments = await _commentCollection
         .Find(c => user.Comments.Contains(c.Id))
         .Project(c => new Comment
         {
            Id = c.Id,
            CommentText = c.CommentText,
            CreatedAt = c.CreatedAt,
            PostId = c.PostId,
         })
         .ToListAsync();

      return new UserDetailsResponse
      {
         Id = user.Id,
         Bio = user.Bio,
         Avatar = user.Avatar,
         CreatedAt = user.CreatedAt,
         Username = user.UserName,
         Name = user.Name,
         Comments = comments,
         Posts = posts
      };
   }
}