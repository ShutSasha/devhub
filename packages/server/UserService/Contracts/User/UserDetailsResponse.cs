using System.Text.Json.Serialization;
using UserService.Models.User;

namespace UserService.Contracts.User;

public class UserDetailsResponse
{
   [JsonPropertyName("_id")]
   public string Id { get; set; }
   public string Bio { get; set; }
   public string Avatar { get; set; }
   public string Name { get; set; }
   public string Username { get; set; }
   public List<string> Followers { get; set; }
   public List<string> Followings { get; set; }
   public DateTime CreatedAt { get; set; }
   public List<Models.User.Post> Posts { get; set; }
   public List<Comment> Comments { get; set; }
}
