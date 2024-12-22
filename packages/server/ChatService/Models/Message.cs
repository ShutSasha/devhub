using System.Text.Json.Serialization;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace ChatService.Models;

public class Message
{
   [BsonId]
   [BsonRepresentation(BsonType.ObjectId)]
   [JsonPropertyName("_id")]
   public string Id { get; set; }

   [BsonElement("chat")]
   [BsonRepresentation(BsonType.ObjectId)]
   public string Chat { get; set; }

   [BsonElement("userSender")]
   [BsonRepresentation(BsonType.ObjectId)]
   public string UserSender { get; set; }

   [BsonElement("content")]
   public string Content { get; set; }

   [BsonElement("createdAt")]
   [BsonRepresentation(BsonType.DateTime)]
   public DateTime CreatedAt { get; set; }
}