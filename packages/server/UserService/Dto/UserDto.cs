using System.Text.Json.Serialization;

namespace UserService.Dto;

public class UserDto
{
   [JsonPropertyName("_id")]
   public string Id { get; set; }
   public string Name { get; set; }
   public string Username { get; set; }
   public string Avatar { get; set; }
   public string Email { get; set; }
   public string Bio { get; set; }
   public DateTime CreatedAt { get; set; }
   public int DevPoints { get; set; } 
   public string? ActivationCode { get; set; }
   public bool IsActivated { get; set; }
   public List<string> UserRole { get; set; }
}