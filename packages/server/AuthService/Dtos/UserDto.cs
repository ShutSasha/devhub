using System.Text.Json.Serialization;
using AuthService.Models.Enums;
using Newtonsoft.Json;

namespace AuthService.Dtos;

public class UserDto
{
   [JsonPropertyName("_id")]
   public string Id { get; set; }
   public string Name { get; set; }
   [JsonProperty("username")]
   public string Username { get; set; }
   public string Avatar { get; set; }
   public string Email { get; set; }
   public DateTime CreatedAt { get; set; }
   public int DevPoints { get; set; } 
   public string? ActivationCode { get; set; }
   public bool IsActivated { get; set; }
   public List<string> UserRole { get; set; }
}