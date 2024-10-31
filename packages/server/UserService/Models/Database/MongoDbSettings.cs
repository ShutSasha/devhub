namespace UserService.Models.Database;

public class MongoDbSettings
{
   public string ConnectionUri { get; set; } = null!;
   public string DatabaseName { get; set; } = null!;
   public string CollectionName { get; set; } = null!;
}