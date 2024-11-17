using System.ComponentModel.DataAnnotations;

namespace UserService.Contracts.User;

public record EditUserRequest
{
   public string Id { get; set; }
   public string Name { get; set; }
   public string Bio { get; set; }
   public List<string> Tags { get; set; }
}