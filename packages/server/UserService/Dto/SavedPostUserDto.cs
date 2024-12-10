using System.Text.Json.Serialization;

namespace UserService.Dto;

public class SavedPostUserDto
{
   [JsonPropertyName("_id")]
   public string Id { get; set; }
   public string Name { get; set; }
   public string Username { get; set; }
   public string Avatar { get; set; }
   public int DevPoints { get; set; }
}