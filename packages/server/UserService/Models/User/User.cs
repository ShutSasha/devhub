using System.Text.Json.Serialization;
using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;
using UserService.Models.Enums;

namespace UserService.Models.User;

[BsonIgnoreExtraElements]
public class User
{
   [BsonId]
   [BsonRepresentation(BsonType.ObjectId)]
   [BsonElement("_id")] 
   [JsonPropertyName("_id")]
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
   
   [BsonElement("bio")] 
   public string Bio { get; set; }
   
   [BsonElement("createdAt")] 
   [BsonRepresentation(BsonType.DateTime)]
   public DateTime CreatedAt { get; set; } = DateTime.Now;
   
   [BsonElement("devPoints")] 
   public int DevPoints { get; set; } = 0;
   
   [BsonElement("activationCode")]
   public string? ActivationCode { get; set; }
   
   [BsonElement("isActivated")]
   public bool IsActivated { get; set; } = false;

   [BsonElement("roles")]
   [BsonRepresentation(BsonType.String)]
   public List<UserRole> UserRole { get; set; } = new List<UserRole> { Enums.UserRole.User };
   
   [BsonElement("posts")]
   [BsonRepresentation(BsonType.ObjectId)]
   public List<string> Posts { get; set; } = new List<string>();
   
   // [BsonElement("marks")]
   // [BsonRepresentation(BsonType.ObjectId)]
   // public List<string> Reports { get; set; } = new List<string>();

   [BsonElement("tags")]
   [BsonRepresentation(BsonType.String)]
   public List<string> Tags { get; set; } = new List<string>();
   
   [BsonElement("comments")]
   [BsonRepresentation(BsonType.ObjectId)]
   public List<string> Comments { get; set; } = new List<string>();
   
   [BsonElement("friends")]
   [BsonRepresentation(BsonType.ObjectId)]
   public List<string> Friends { get; set; } = new List<string>();
   
   [BsonElement("chats")]
   [BsonRepresentation(BsonType.ObjectId)]
   public List<string> Chats { get; set; } = new List<string>();
   
   [BsonElement("reports")]
   [BsonRepresentation(BsonType.ObjectId)]
   public List<string> Reports { get; set; } = new List<string>();
   
   
}