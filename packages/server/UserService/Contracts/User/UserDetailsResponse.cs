using System.Text.Json.Serialization;
using UserService.Models.User;

namespace UserService.Contracts.User;

public class UserDetailsResponse
{
   public string Id { get; set; }
   public string Bio { get; set; }
   public string Avatar { get; set; }
   public string Name { get; set; }
   public DateTime CreatedAt { get; set; }
   public List<Post> Posts { get; set; }
   public List<Comment> Comments { get; set; }
}
