using System.Text.Json.Serialization;

namespace UserService.Dto;

public class SavedPostCommentsDto
{
   [JsonPropertyName("_id")]
   public string Id { get; set; }

   public SavedPostUserDto User { get; set; }

   public string Post { get; set; }
   public string CommentText { get; set; }
   public DateTime CreatedAt { get; set; }
}