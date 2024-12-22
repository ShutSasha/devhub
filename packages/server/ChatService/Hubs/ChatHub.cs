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
      string? chatId = null;
      
      chatId = await _chatService.IsChatExsist(userId, targetUserId);

      if (string.IsNullOrEmpty(chatId))
      {
         chatId = await _chatService.CreateChat(userId, targetUserId);
      }

      await Groups.AddToGroupAsync(Context.ConnectionId, chatId);
      await Clients.Caller.SendAsync("JoinedChat", chatId);
   }

   public async Task SendMessage(string chatId, string userId, string content)
   {

      await _messageService.AddMessageToChat(chatId, userId, content);

      await Clients.Group(chatId).SendAsync("ReceiveMessage", new
      {
         _Id = chatId,
         UserSender = userId,
         Chat = chatId,
         Content = content,
         CreatedAt = DateTime.Now,
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