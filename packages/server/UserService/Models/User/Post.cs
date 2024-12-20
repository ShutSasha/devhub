using System.Text.Json.Serialization;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using UserService.Dto;

namespace UserService.Models.User;
public class Post
{
   [BsonId]
   [BsonRepresentation(BsonType.ObjectId)]
   [JsonPropertyName("_id")]
   public string Id { get; set; }
   [BsonElement("title")]
   public string Title { get; set; }
   
   [BsonElement("user")] 
   [BsonRepresentation(BsonType.ObjectId)]
   public ObjectId UserId { get; set; }
   
   [BsonElement("content")]
   public string Content { get; set; }
   [BsonElement("headerImage")]
   public string HeaderImage { get; set; }
   [BsonElement("createdAt")]
   public DateTime CreatedAt { get; set; }
   [BsonElement("likes")]
   public int Likes { get; set; }
   [BsonElement("dislikes")]
   public int Dislikes { get; set; }
   
   [BsonElement("saved")]
   public int Saved { get; set; }
   
   [BsonElement("tags")] 
   public List<string> Tags { get; set; }

   [BsonElement("comments")]
   [BsonRepresentation(BsonType.ObjectId)]
   public List<string> Comments { get; set; }
   
   [BsonElement("reports")]
   [BsonRepresentation(BsonType.ObjectId)]
   public List<string> Reports { get; set; }
}