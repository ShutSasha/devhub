using System.ComponentModel.DataAnnotations;
using AuthService.Helpers.Security;

namespace AuthService.Contracts.Email;

public class ChangePasswordRequest
{
   [Required]
   public string Email { get; set; }
      
   [Required]
   [StringLength(20,ErrorMessage ="{0} length must be more than {2} and less than {1} characters", MinimumLength = 8) ]
   public string Password { get; set; }

   [Required]
   [ComparePasswords("Password",ErrorMessage ="{0} doesn't match")]
   public string RepeatPassword { get; set; }
}