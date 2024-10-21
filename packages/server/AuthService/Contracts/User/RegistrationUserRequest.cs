using System.ComponentModel.DataAnnotations;

namespace AuthService.Contracts.User;

public record RegistrationUserRequest(
   [Required] string UserName,
   [Required] string Password,
   [Required] string Email
);