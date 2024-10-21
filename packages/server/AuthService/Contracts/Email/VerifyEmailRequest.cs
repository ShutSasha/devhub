namespace AuthService.Controllers;

public record VerifyEmailRequest
{
   public string ActivationCode { get; set; }
}