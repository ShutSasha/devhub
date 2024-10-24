namespace AuthService.Contracts.Token;  
  
public record TokenModel(  
   string RefreshToken,  
   string AccessToken  
);