using Microsoft.Extensions.Options;
using MongoDB.Bson;
using MongoDB.Driver;
using MongoDB.Driver.Linq;
using UserService.Abstracts;
using UserService.Contracts.Posts;
using UserService.Contracts.User;
using UserService.Models.Database;
using UserService.Models.User;

namespace UserService.Services;

public class UserService : IUserService
{
   private readonly ILogger<UserService> _logger;
   private readonly IStorageService _storageService;
   private readonly IMongoCollection<User> _userCollection;
   private readonly IMongoCollection<Post> _postCollection;
   private readonly IMongoCollection<Comment> _commentCollection;


   public UserService(IMongoDatabase mongoDatabase, IOptions<MongoDbSettings> mongoDbSettings,
      ILogger<UserService> logger, IStorageService storageService
      )
   {
      _logger = logger;
      _storageService = storageService;
      _commentCollection = mongoDatabase.GetCollection<Comment>("comments");
      _postCollection = mongoDatabase.GetCollection<Post>("posts");
      _userCollection = mongoDatabase.GetCollection<User>(mongoDbSettings.Value.CollectionName);
   }

   public async Task EditUser(string id, string name, string bio, List<string> tags)
   {
      var filter = Builders<User>.Filter.Eq(u => u.Id, id);
      var user = await _userCollection.Find(filter).FirstOrDefaultAsync();

      if (user == null)
      {
         _logger.LogError($"User with ID {id} not found.");
         throw new Exception($"404:User with ID {id} not found.");
      }

      var update = Builders<User>.Update
         .Set(u => u.Name, name)
         .Set(u => u.Bio, bio)
         .Set(u => u.Tags, tags);

      var result = await _userCollection.UpdateOneAsync(filter, update);

      if (result.ModifiedCount == 0)
      {
         _logger.LogWarning($"No changes were made to user with ID {id}.");
         throw new Exception($"400:No changes were made to user with ID {id}.");
      }

      _logger.LogInformation($"User with ID {id} updated successfully.");
   }

   public async Task EditUserIcon(string id, string fileName, Stream fileStream, string contentType)
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
         _logger.LogInformation($"404:User {id} avatar updated successfully.");
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

   public async Task<UserDetailsResponse> GetUserDetailsById(string id)
   {
      var user = await GetById(id);
      
      var posts = await _postCollection
         .Find(p => user.Posts.Contains(p.Id))
         .Project(p => new Post // Создаем проекцию
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