using System.Text.Json.Serialization;
using AuthService.Models.Enums;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace AuthService.Models;

[BsonIgnoreExtraElements]
public class User
{
   [BsonId]
   [BsonRepresentation(BsonType.ObjectId)]
   [BsonElement("id")] 
   [JsonPropertyName("id")]
   public string Id { get; set; }
   
   [BsonElement("name")] 
   public string Name { get; set; }
   
   [BsonElement("username")] 
   public string UserName { get; set; }
   
   [BsonElement("password")]
   public string Password { get; set; }
   
   [BsonElement("avatar")] 
   public string Avatar { get; set; }
   
   [BsonElement("email")] 
   public string Email { get; set; }
   
   [BsonElement("createdAt")] 
   public DateTime CreatedAt { get; set; } = DateTime.UtcNow;
   
   [BsonElement("devPoints")] 
   public int DevPoints { get; set; } = 0;
   
   [BsonElement("activationCode")]
   public string? ActivationCode { get; set; }
   
   [BsonElement("isActivated")]
   public bool IsActivated { get; set; } = false;

   [BsonElement("roles")]
   [BsonRepresentation(BsonType.String)]
   public List<string> UserRole { get; set; } = new List<string> { Enums.UserRole.User.ToString() };
   
}
