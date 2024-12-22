using ChatService.Contracts;
using ChatService.Models;

namespace ChatService.Abstractions;

public interface IChatService
{
   Task<Chat> GetById(string chatId); 
   Task<string> CreateChat(string userId, string targetUserId);
   Task DeleteChat(string chatId);
   Task<List<UserChatPreview>> GetUserChats(string userId);
   Task<ChatResponse> GetChat(string chatId,string userId);
   Task<string?> IsChatExsist(string userId, string targetUserId);
   Task<ChatResponse> GetFirstChat(string userId);

}