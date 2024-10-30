namespace AuthService.Contracts.Email;

public record VerifyEmailRequest
{
   public string Email { get; set; }
   public string ActivationCode { get; set; }
}