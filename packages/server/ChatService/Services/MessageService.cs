using ChatService.Abstractions;
using ChatService.Models;
using MongoDB.Driver;
using Exception = System.Exception;

namespace ChatService.Services;

public class MessageService : IMessageService
{
   private readonly IMongoCollection<Message> _messsageCollection;
   private readonly IMongoCollection<Chat> _chatCollection;
   private readonly IChatService _chatService;
   
   public MessageService(IMongoDatabase mongoDatabase, IChatService messageService)
   {
      _chatService = messageService;
      _messsageCollection = mongoDatabase.GetCollection<Message>("messages");
      _chatCollection = mongoDatabase.GetCollection<Chat>("chats");
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
   
   public async Task AddMessageToChat(string chatId, string senderId, string content)
   {
      var chat = await _chatService.GetById(chatId);
      if (chat == null)
      {
         throw new Exception("404: Chat not found");
      }
      
      var message = new Message
      {
         Chat = chatId,
         CreatedAt = DateTime.Now,
         UserSender = senderId,
         Content = content,
      };
      
      await _messsageCollection.InsertOneAsync(message);

      var update = Builders<Chat>.Update.Push(c => c.Messages, message.Id);

      await _chatCollection.UpdateOneAsync(c => c.Id == chatId, update);
   }
}