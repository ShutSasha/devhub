using AuthService.Models.Enums;
namespace AuthService.Dtos;

public class UserDto
{
   public string Id { get; set; }
   public string Name { get; set; }
   public string UserName { get; set; }
   public string Avatar { get; set; }
   public string Email { get; set; }
   public DateTime CreatedAt { get; set; } = DateTime.Now;
   public int DevPoints { get; set; } = 0;
   public string? ActivationCode { get; set; }
   public bool IsActivated { get; set; } = false;
   public List<string> UserRole { get; set; } = new List<string> { Models.Enums.UserRole.User.ToString() };
}