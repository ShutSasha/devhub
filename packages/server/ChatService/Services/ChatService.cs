using ChatService.Abstractions;
using ChatService.Models;
using ChatService.Models.Database;
using ChatService.Models.Enums;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using ChatService.Models;
using Exception = System.Exception;

namespace ChatService.Services;

public class ChatService : IChatService
{
   private readonly UserChatService.UserChatServiceClient _chatServiceClient;
   private readonly IMongoCollection<Chat> _chatCollection;
   private readonly IMongoCollection<User> _userCollection;
   private readonly IMongoCollection<Message> _messageCollection;

   public ChatService(IMongoDatabase mongoDatabase, IOptions<MongoDbSettings> options,
      UserChatService.UserChatServiceClient chatServiceClient)
   {
      _chatServiceClient = chatServiceClient;
      _chatCollection = mongoDatabase.GetCollection<Chat>(options.Value.CollectionName);
      _userCollection = mongoDatabase.GetCollection<User>("users");
      _messageCollection = mongoDatabase.GetCollection<Message>("messages");
   }


   public async Task<Chat> GetById(string chatId)
   {
      var candidate = await _chatCollection
         .Find(c => c.Id == chatId)
         .FirstOrDefaultAsync();

      if (candidate == null)
         throw new Exception("404: Chat wasn't found");

      return candidate;
   }

   public async Task<string> CreateChat(string userId, string targetUserId)
   {
      var chat = new Chat
      {
         CreatedAt = DateTime.Now,
         Participants = new List<string> { userId, targetUserId },
         Messages = new List<string>(),
      };

      await _chatCollection.InsertOneAsync(chat);

      var addChatToUserResponse = _chatServiceClient.AddChatToUser(new AddChatToUserRequest
      {
         ChatId = chat.Id,
         UserId = userId,
         TargetUserId = targetUserId
      });

      if (!addChatToUserResponse.Success)
      {
         await _chatCollection.DeleteOneAsync(c => c.Id == chat.Id);
         throw new Exception(addChatToUserResponse.Message);
      }

      return chat.Id;
   }

   public async Task DeleteChat(string chatId)
   {
      var chat = await GetById(chatId);

      var chatDeletionFromUsersResult = _chatServiceClient
         .DeleteChatToUserRequest(new DeleteChatFromUserRequest
         {
            ChatId = chatId,
            TargetUserId = chat.Participants[0],
            UserId = chat.Participants[1]
         });

      if (!chatDeletionFromUsersResult.Success)
         throw new Exception("500: " + chatDeletionFromUsersResult.Message);

      await _chatCollection.DeleteOneAsync(c => c.Id == chatId);
   }

   public async Task<List<UserChatPreview>> GetUserChats(string userId)
   {
      var userChats = await _chatCollection
         .Find(chat => chat.Participants.Contains(userId))
         .ToListAsync();

      if (userChats == null || !userChats.Any())
         return new List<UserChatPreview>();

      var chatPreviews = new List<UserChatPreview>();

      foreach (var chat in userChats)
      {
         var lastMessage = chat.Messages.LastOrDefault();

         var participantsDetails = new ParticipantDetail();
         foreach (var participantId in chat.Participants.Where(p => p != userId))
         {
            var user = await _userCollection
               .Find(u => u.Id == participantId)
               .FirstOrDefaultAsync();
            participantsDetails = new ParticipantDetail
            {
               Id = user.Id,
               UserName = user.Name,
               AvatarUrl = user.Avatar,
            };
         }

         chatPreviews.Add(new UserChatPreview
         {
            ChatId = chat.Id,
            LastMessage = lastMessage,
            Participants = participantsDetails,
         });
      }

      return chatPreviews.OrderByDescending(c => c.Timestamp).ToList();
   }

   public async Task AddMessageToChat(string chatId, string senderId, string content)
   {
      var chat = await GetById(chatId);

      var message = new Message
      {
         Chat = chatId,
         CreatedAt = DateTime.Now,
         UserSender = senderId,
      };

      await _messageCollection.InsertOneAsync(message);

      chat.Messages.Add(message.Id);

      await _chatCollection.ReplaceOneAsync(c => c.Id == chatId, chat);
   }
}