using Microsoft.Extensions.Options;
using MongoDB.Driver;
using UserService.Abstracts;
using UserService.Models.Database;
using UserService.Models.User;

namespace UserService.Services;

public class UserService : IUserService
{
   private readonly ILogger<UserService> _logger;
   private readonly IStorageService _storageService;
   private readonly IMongoCollection<User> _userCollection;
   

   public UserService(IMongoDatabase mongoDatabase, IOptions<MongoDbSettings> mongoDbSettings,
      ILogger<UserService> logger, IStorageService storageService)
   {
      _logger = logger;
      _storageService = storageService;
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
      
      var candidate = await _userCollection.Find(u => u.Id == id).FirstOrDefaultAsync();
    
      if (candidate == null)
      {
         _logger.LogError($"404: User with this {id} not found");
         throw new Exception($"404: User with this {id} not found");
      }
      
      var updatePictureResult = await _storageService.UploadFileAsync(id, fileName, fileStream, contentType);
      
      candidate.Avatar = updatePictureResult;
      
      var filter = Builders<User>.Filter.Eq(u => u.Id, id);
      var update = Builders<User>.Update.Set(u => u.Avatar, candidate.Avatar);

      var result = await _userCollection.UpdateOneAsync(filter, update);

      if (result.ModifiedCount > 0)
      {
         _logger.LogInformation($"User {id} avatar updated successfully.");
      }
      else
      {
         _logger.LogError($"Failed to update avatar for user {id}.");
         throw new Exception($"Failed to update avatar for user {id}.");
      }
      
   }

}