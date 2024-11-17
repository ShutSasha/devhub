using UserService.Models.User;

namespace UserService.Abstracts;

public interface IUserService
{
   Task EditUser(string Id, string Name, string Bio, List<string> tags);
   Task EditUserIcon(string id, string fileName, Stream fileStream, string contentType);
   Task<User> GetById(string id);
}