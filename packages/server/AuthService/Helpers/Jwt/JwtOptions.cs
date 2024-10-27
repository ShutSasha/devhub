namespace AuthService.Helpers.Jwt;  
  
public class JwtOptions  
{  
   public string AccessSecretKey { get; set; } = string.Empty;  
   public string RefreshSecretKey { get; set; } = string.Empty;  
   public int ExpiresDuration { get; set; } = 30;
   public int RefreshExpiresDuration { get; set; } = 30;
}