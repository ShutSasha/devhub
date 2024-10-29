using System.ComponentModel.DataAnnotations;

namespace AuthService.Contracts.Email;

public record SendVerificationCodeRequest(
   [Required]
   [EmailAddress]
   string Email
);