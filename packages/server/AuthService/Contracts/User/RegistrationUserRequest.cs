using System.ComponentModel.DataAnnotations;
using AuthService.Helpers.Security;

namespace AuthService.Contracts.User;

public record RegistrationUserRequest(
   
   [Required] 
   [StringLength(50,ErrorMessage= "{0} length must be more than {2} and less than {1} characters",MinimumLength = 3)]
   string Username,
   
   [Required]
   [StringLength(20,ErrorMessage ="{0} length must be more than {2} and less than {1} characters", MinimumLength = 8) ]
   string Password,
   
   [Required]
   [ComparePasswords("Password",ErrorMessage ="{0} doesn't match")]
   string RepeatPassword,
   
   [Required]
   [EmailAddress]
   string Email
);