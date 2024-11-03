using AuthService.Models.Enums;
using Newtonsoft.Json;

namespace AuthService.Dtos;

public class UserDto
{
   [JsonProperty("_id")]
   public string Id { get; set; }
   public string Name { get; set; }
   public string UserName { get; set; }
   public string Avatar { get; set; }
   public string Email { get; set; }
   public DateTime CreatedAt { get; set; } = DateTime.Now;
   public int DevPoints { get; set; } = 0;
   public string? ActivationCode { get; set; }
   public bool IsActivated { get; set; } = false;
   public List<UserRole> UserRole { get; set; } = new List<UserRole> { Models.Enums.UserRole.User };
}