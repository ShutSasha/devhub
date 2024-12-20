using System.Text.Json.Serialization;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using UserService.Models.User;

namespace UserService.Dto;

public class PostDto
{
   [JsonPropertyName("_id")]
   public string Id { get; set; }
   public string Title { get; set; }
   public SavedPostUserDto User { get; set; }
   public string Content { get; set; }
   public string HeaderImage { get; set; }
   public DateTime CreatedAt { get; set; }
   public int Likes { get; set; }
   public int Dislikes { get; set; }
   public int Saved { get; set; }
   public List<string> Tags { get; set;}
   public List<string> Comments { get; set; }
   public List<string> Tags { get; set;}
}