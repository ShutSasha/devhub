namespace AuthService.Controllers;

public record VerifyEmailRequest
{
   public string Email { get; set; }
   public string ActivationCode { get; set; }
}