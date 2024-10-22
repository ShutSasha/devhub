using System.ComponentModel.DataAnnotations;

namespace AuthService.Contracts.User;

public record RegistrationUserRequest(
   
   [Required] 
   [StringLength(50,ErrorMessage= "{0} length must be more than {2} and less than {1} characters",MinimumLength = 3)]
   string UserName,
   
   [Required]
   [StringLength(20,ErrorMessage ="{0} length must be more than {2} and less than {1} characters", MinimumLength = 8) ]
   string Password,
   
   [Required]
   [EmailAddress]
   string Email
);