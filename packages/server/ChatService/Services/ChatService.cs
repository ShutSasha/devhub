using ChatService.Abstractions;
using ChatService.Contracts;
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
         var lastMessage = await _messageCollection
            .Find(m => m.Chat == chat.Id)
            .SortByDescending(m => m.CreatedAt)
            .FirstOrDefaultAsync();

         var participantsDetails = new ParticipantDetail();
         foreach (var participantId in chat.Participants.Where(p => p != userId))
         {
            var user = await _userCollection
               .Find(u => u.Id == participantId)
               .FirstOrDefaultAsync();
            participantsDetails = new ParticipantDetail
            {
               Id = user.Id,
               Username = user.Name,
               AvatarUrl = user.Avatar,
            };
         }

         chatPreviews.Add(new UserChatPreview
         {
            ChatId = chat.Id,
            LastMessage = lastMessage?.Content ?? "No messages yet",
            Participants = participantsDetails,
         });
      }

      return chatPreviews.OrderByDescending(c => c.Timestamp).ToList();
   }

   public async Task<ChatResponse> GetChat(string chatId, string userId)
   {
      var chat = await _chatCollection
         .Find(c => c.Id == chatId)
         .FirstOrDefaultAsync();

      if (chat == null)
         throw new Exception("404: Chat not found");

      var otherParticipantId = chat.Participants
         .FirstOrDefault(p => p != userId);

      if (otherParticipantId == null)
         throw new Exception("Chat does not have another participant");

      var otherParticipant = await _userCollection
         .Find(u => u.Id == otherParticipantId)
         .FirstOrDefaultAsync();

      if (otherParticipant == null)
         throw new Exception("404: Participant not found");

      var participantsDetails = new ParticipantDetail
      {
         Id = otherParticipant.Id,
         Username = otherParticipant.Name,
         AvatarUrl = otherParticipant.Avatar,
      };

      var messages = await _messageCollection
         .Find(m => m.Chat == chatId)
         .SortBy(m => m.CreatedAt)
         .ToListAsync();

      messages ??= new List<Message>();

      var chatResponse = new ChatResponse
      {
         ChatId = chatId,
         ChatMessages = messages,
         ParticipantDetails = participantsDetails
      };

      return chatResponse;
   }

   public async Task<string> IsChatExsist(string userId, string targetUserId)
   {
      var existingChat = await _chatCollection
         .Find(chat => chat.Participants.Contains(userId) && chat.Participants.Contains(targetUserId))
         .FirstOrDefaultAsync();

      return existingChat.Id;
   }

   public async Task<ChatResponse> GetFirstChat(string userId)
   {
      var userChats = await _chatCollection
         .Find(chat => chat.Participants.Contains(userId))
         .ToListAsync();

      if (userChats == null || !userChats.Any())
      {
         throw new Exception("404: No chats found for the user.");
      }

      var latestMessage = await _messageCollection
         .Find(message => userChats.Select(chat => chat.Id).Contains(message.Chat))
         .SortByDescending(m => m.CreatedAt)
         .FirstOrDefaultAsync();

      if (latestMessage == null)
      {
         throw new Exception("404: No messages found in any chat.");
      }

      var latestChat = userChats.FirstOrDefault(chat => chat.Id == latestMessage.Chat);

      if (latestChat == null)
      {
         throw new Exception("404: Chat for the latest message not found.");
      }

      var targetUserId = latestChat.Participants.FirstOrDefault(participantId => participantId != userId);

      if (string.IsNullOrEmpty(targetUserId))
      {
         throw new Exception("404: No target user found in the chat.");
      }

      var targetUser = await _userCollection
         .Find(u => u.Id == targetUserId)
         .FirstOrDefaultAsync();

      if (targetUser == null)
      {
         throw new Exception("404: Target user not found.");
      }

      var participantDetails = new ParticipantDetail
      {
         Id = targetUser.Id,
         Username = targetUser.Name,
         AvatarUrl = targetUser.Avatar
      };

      var chatMessages = await _messageCollection
         .Find(m => m.Chat == latestChat.Id)
         .SortBy(m => m.CreatedAt) // Сортируем по дате
         .ToListAsync();

      var chatResponse = new ChatResponse
      {
         ChatId = latestChat.Id,
         ChatMessages = chatMessages,
         ParticipantDetails = participantDetails
      };

      return chatResponse;
   }
}