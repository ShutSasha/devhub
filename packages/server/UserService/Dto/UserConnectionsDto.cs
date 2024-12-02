using System.Text.Json.Serialization;

namespace UserService.Dto;

public class UserConnectionsDto
{
   [JsonPropertyName("_id")]
   public string Id { get; set; }
   public string Username { get; set; }
   public string Avatar { get; set; }
}