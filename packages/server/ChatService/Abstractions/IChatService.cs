using ChatService.Models;

namespace ChatService.Abstractions;

public interface IChatService
{
   Task<Chat> GetById(string chatId); 
   Task<string> CreateChat(string userId, string targetUserId);
   Task DeleteChat(string chatId);
   Task<List<UserChatPreview>> GetUserChats(string userId);

}