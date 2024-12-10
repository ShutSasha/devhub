using ChatService;
using Grpc.Core;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using UserService.Models.Database;
using UserService.Models.User;

namespace UserService.Services;

public class UserChatService : ChatService.UserChatService.UserChatServiceBase
{
    private readonly IMongoCollection<User> _userCollection;

    public UserChatService(IMongoDatabase mongoDatabase, IOptions<MongoDbSettings> options)
    {
        _userCollection = mongoDatabase.GetCollection<User>(options.Value.CollectionName);
    }

    public override async Task<ChatService.UserResponse> AddChatToUser(AddChatToUserRequest request, ServerCallContext context)
    {
        var user = await _userCollection.Find(u => u.Id == request.UserId).FirstOrDefaultAsync();
        if (user == null)
        {
            return new ChatService.UserResponse
            {
                Success = false,
                Message = $"User with ID {request.UserId} not found."
            };
        }

        var targetUser = await _userCollection.Find(u => u.Id == request.TargetUserId).FirstOrDefaultAsync();
        if (targetUser == null)
        {
            return new ChatService.UserResponse
            {
                Success = false,
                Message = $"Target user with ID {request.TargetUserId} not found."
            };
        }

        bool userAlreadyHasChat = user.Chats.Contains(request.ChatId);
        bool targetUserAlreadyHasChat = targetUser.Chats.Contains(request.ChatId);

        if (userAlreadyHasChat && targetUserAlreadyHasChat)
        {
            return new ChatService.UserResponse
            {
                Success = false,
                Message = "Chat already exists for both users."
            };
        }

        var updates = new List<Task>();

        if (!userAlreadyHasChat)
        {
            updates.Add(UpdateUserChats(request.UserId, request.ChatId, true));
        }

        if (!targetUserAlreadyHasChat)
        {
            updates.Add(UpdateUserChats(request.TargetUserId, request.ChatId, true));
        }

        await Task.WhenAll(updates);

        return new ChatService.UserResponse
        {
            Success = true,
            Message = "Chat successfully added to both users."
        };
    }

    public override async Task<ChatService.UserResponse> DeleteChatToUserRequest(DeleteChatFromUserRequest request, ServerCallContext context)
    {
        var user = await _userCollection.Find(u => u.Id == request.UserId).FirstOrDefaultAsync();
        if (user == null)
        {
            return new ChatService.UserResponse
            {
                Success = false,
                Message = $"User with ID {request.UserId} not found."
            };
        }

        var targetUser = await _userCollection.Find(u => u.Id == request.TargetUserId).FirstOrDefaultAsync();
        if (targetUser == null)
        {
            return new ChatService.UserResponse
            {
                Success = false,
                Message = $"Target user with ID {request.TargetUserId} not found."
            };
        }

        var updates = new List<Task>();

        if (user.Chats.Contains(request.ChatId))
        {
            updates.Add(UpdateUserChats(request.UserId, request.ChatId, false));
        }

        if (targetUser.Chats.Contains(request.ChatId))
        {
            updates.Add(UpdateUserChats(request.TargetUserId, request.ChatId, false));
        }

        await Task.WhenAll(updates);

        return new ChatService.UserResponse
        {
            Success = true,
            Message = "Chat successfully removed from both users."
        };
    }

    private async Task<bool> UpdateUserChats(string userId, string chatId, bool add)
    {
        var update = add
            ? Builders<User>.Update.AddToSet(u => u.Chats, chatId)
            : Builders<User>.Update.Pull(u => u.Chats, chatId);

        var result = await _userCollection.UpdateOneAsync(u => u.Id == userId, update);
        return result.ModifiedCount > 0;
    }
}
