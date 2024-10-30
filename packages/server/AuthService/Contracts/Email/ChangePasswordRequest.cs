using System.ComponentModel.DataAnnotations;
using AuthService.Helpers.Security;

namespace AuthService.Contracts.Email;

public class ChangePasswordRequest
{
   [Required]
   public string Email { get; set; }
      
   [Required]
   public string Password { get; set; }

   [Required]
   [ComparePasswords("Password",ErrorMessage ="{0} doesn't match")]
   public string RepeatPassword { get; set; }
}