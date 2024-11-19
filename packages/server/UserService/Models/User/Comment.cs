using System.Text.Json.Serialization;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace UserService.Models.User;

public class Comment
{
   [BsonId]
   [BsonRepresentation(BsonType.ObjectId)]
   [JsonPropertyName("_id")]
   public string Id { get; set; }
   [BsonElement("post")]
   [BsonRepresentation(BsonType.ObjectId)]
   public string PostId { get; set; }
   [BsonElement("commentText")]
   public string CommentText { get; set; }
   [BsonElement("createdAt")] 
   public DateTime CreatedAt { get; set; }
}