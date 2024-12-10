using ChatService.Abstractions;
using ChatService.Models;
using MongoDB.Driver;
using Exception = System.Exception;

namespace ChatService.Services;

public class MessageService : IMessageService
{
   private readonly IMongoCollection<Message> _messsageCollection;
   
   public MessageService(IMongoDatabase mongoDatabase)
   {
      _messsageCollection = mongoDatabase.GetCollection<Message>("messages");
   }


   public async Task<Message> GetById(string messageId)
   {
      var candidate = await _messsageCollection
         .Find(m => m.Id == messageId)
         .FirstOrDefaultAsync();

      if (candidate == null)
         throw new Exception("404: Message not found");

      return candidate;
   }

   public async Task<List<Message>> GetMessagesByChatId(string chatId)
   {
      var chatMessages = await _messsageCollection
         .Find(m => m.Chat == chatId)
         .ToListAsync();

      if (chatMessages == null)
         return new List<Message>();

      return chatMessages;
   }
   
}