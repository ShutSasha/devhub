using AuthService.Dtos;

namespace AuthService.Contracts.User;  
  
public record LoginUserResponse  
{  
   public string AccessToken { get; set; }  
   public string RefreshToken { get; internal set; }
   public UserDto UserData { get; set; }
}
