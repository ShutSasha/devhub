using MongoDB.Bson;
using MongoDB.Bson.Serialization.Attributes;

namespace AuthService.Models;

[BsonIgnoreExtraElements]
public class User
{
   [BsonId]
   [BsonRepresentation(BsonType.ObjectId)]
   public string id { get; set; }
   public string name { get; set; }
   public string userName { get; set; }
   public string avatar { get; set; }
   [BsonRepresentation(BsonType.DateTime)]
   public DateTime createdAt { get; set; } = DateTime.Now;
   public int devPoints { get; set; } = 0;
   public string activationLink { get; set; }
   public bool isActivated { get; set; }
}