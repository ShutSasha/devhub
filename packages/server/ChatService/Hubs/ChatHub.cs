using ChatService.Abstractions;
using Microsoft.AspNetCore.SignalR;

namespace ChatService.Hubs;

public class ChatHub : Hub
{
   private readonly IChatService _chatService;
   private readonly IMessageService _messageService;

   public ChatHub(IChatService chatService, IMessageService messageService)
   {
      _chatService = chatService;
      _messageService = messageService;
   }

   public async Task JoinChat(string userId, string targetUserId)
   {
      var chatId = await _chatService.CreateChat(userId, targetUserId);

      await Groups.AddToGroupAsync(Context.ConnectionId, chatId);
   }

   public async Task SendMessage(string chatId, string userId, string content)
   {

      await _messageService.AddMessageToChat(chatId, userId, content);

      await Clients.Group(chatId).SendAsync("ReceiveMessage", new
      {
         ChatId = chatId,
         SenderId = userId,
         Content = content,
         Timestamp = DateTime.Now,
      });
   }
   
   public override async Task OnConnectedAsync()
   {
      var userId = Context.UserIdentifier;
      if (!string.IsNullOrEmpty(userId))
      {
         await Groups.AddToGroupAsync(Context.ConnectionId, userId);
      }
      await base.OnConnectedAsync();
   }

   public override async Task OnDisconnectedAsync(Exception? exception)
   {
      var userId = Context.UserIdentifier;
      if (!string.IsNullOrEmpty(userId))
      {
         await Groups.RemoveFromGroupAsync(Context.ConnectionId, userId);
      }
      await base.OnDisconnectedAsync(exception);
   }
}