namespace AuthService.Contracts.User;  
  
public record LoginUserResponse  
{  
   public bool IsLoggedIn { get; set; } = false;  
   public string JwtToken { get; set; }  
   public string RefreshToken { get; internal set; }  
}