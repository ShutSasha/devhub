using ChatService.Abstractions;
using Microsoft.AspNetCore.SignalR;

namespace ChatService.Hubs;

public class ChatHub : Hub
{
   private readonly IChatService _chatService;

   public ChatHub(IChatService chatService)
   {
      _chatService = chatService;
   }

   public async Task JoinChat(string userId, string targetUserId)
   {
      var chatId = await _chatService.CreateChat(userId, targetUserId);

      await Groups.AddToGroupAsync(Context.ConnectionId, chatId);
   }

   public async Task SendMessage(string chatId, string userId, string content)
   {

      await _chatService.AddMessageToChat(chatId, userId, content);

      await Clients.Group(chatId).SendAsync("ReceiveMessage", new
      {
         ChatId = chatId,
         SenderId = userId,
         Content = content,
         Timestamp = DateTime.Now,
      });
   }
}