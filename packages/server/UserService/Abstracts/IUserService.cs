using UserService.Contracts.Posts;
using UserService.Contracts.User;
using UserService.Models.User;

namespace UserService.Abstracts;

public interface IUserService
{
   Task EditUser(string Id, string Name, string Bio, List<string> tags);
   Task EditUserIcon(string id, string fileName, Stream fileStream, string contentType);
   Task<UserDetailsResponse> GetUserDetailsById(string id);
   Task<User> GetById(string id);
   Task<UserReactionsResponse> GetUserReaction(string userId);
}