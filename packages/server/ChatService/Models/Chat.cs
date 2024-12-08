using System.Text.Json.Serialization;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace ChatService.Models;

public class Chat
{
   [BsonId]
   [BsonRepresentation(BsonType.ObjectId)]
   [JsonPropertyName("_id")]
   public string Id { get; set; }
   
   [BsonRepresentation(BsonType.ObjectId)]
   public List<string> Participants { get; set; }
   public List<string> Messages { get; set; }
   public DateTime CreatedAt { get; set; }
}