using UserService.Contracts.Posts;
using UserService.Contracts.User;
using UserService.Models.User;
using HostingEnvironmentExtensions = Microsoft.Extensions.Hosting.HostingEnvironmentExtensions;

namespace UserService.Abstracts;

public interface IUserService
{
   Task EditUser(string Id, string Name, string Bio);
   Task<string> EditUserIcon(string id, string fileName, Stream fileStream, string contentType);
   Task<UserDetailsResponse> GetUserDetailsById(string id);
   Task<User> GetById(string id);
   Task<UserReactionsResponse> GetUserReaction(string userId);
}