using System.Text.Json.Serialization;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace UserService.Models.Report;

public class Report
{
   [BsonId]
   [BsonRepresentation(BsonType.ObjectId)]
   [BsonElement("_id")]
   [JsonPropertyName("_id")]
   public string Id { get; set; }

   [BsonElement("sender")]
   [BsonRepresentation(BsonType.ObjectId)]
   public string Sender { get; set; }
   
   [BsonElement("content")]
   [BsonRepresentation(BsonType.ObjectId)]
   public string Content { get; set; }

   [BsonElement("category")]
   public string Category { get; set; }
}