using UserService.Contracts.User;
using UserService.Contracts.User;
using UserService.Dto;
using UserService.Models.User;
using HostingEnvironmentExtensions = Microsoft.Extensions.Hosting.HostingEnvironmentExtensions;

namespace UserService.Abstracts;

public interface IUserService
{
   Task<UserDto> EditUser(string Id, string Name, string Bio);
   Task<string> EditUserIcon(string id, string fileName, Stream fileStream, string contentType);
   Task<UserDetailsResponse> GetUserDetailsById(string id);
   Task<User> GetById(string id);
   Task<UserReactionsResponse> GetUserReaction(string userId);
   Task<UserDto> AddUserFollowing(string userId, string followingId);
   Task<UserDto> RemoveUserFollowing(string userId, string followingId);
   Task<List<UserConnectionsDto>> GetUserConnections(string userId,string connectionType);
   Task<bool> CheckUserFollowing(string userId, string targetUserId);
}