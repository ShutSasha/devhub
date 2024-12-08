using ChatService.Models;
using ChatService.Models.Database;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

namespace ChatService.Services;

public class ChatService
{
   private readonly IMongoCollection<Chat> _chatCollection;
      
   public ChatService(IMongoDatabase mongoDatabase, IOptions<MongoDbSettings> options)
   {
      _chatCollection = mongoDatabase.GetCollection<Chat>(options.Value.CollectionName);
   }
   
   
}