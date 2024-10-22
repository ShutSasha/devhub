using System.IdentityModel.Tokens.Jwt;  
using System.Security.Claims;  
using System.Text;  
using AuthService.Models;  
using Microsoft.Extensions.Options;  
using Microsoft.IdentityModel.Tokens;  
  
namespace AuthService.Helpers.Jwt;  
  
public class JwtProvider(IOptions<JwtOptions> options)  
{  
   private readonly JwtOptions _options = options.Value;  
  
   public string Generate(User user)  
   {  
      Claim[] claims = [new("id", user.Id)];  
      var signingCredentials = new SigningCredentials(  
         new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_options.SecretKey)),  
         SecurityAlgorithms.HmacSha256);  
  
      var token = new JwtSecurityToken(  
         claims: claims,  
         signingCredentials: signingCredentials,  
         expires: DateTime.UtcNow.AddHours(_options.ExpiresDuration));  
  
      var tokenValue = new JwtSecurityTokenHandler().WriteToken(token);  
  
      return tokenValue;  
   }  
  
   public ClaimsPrincipal GetPrincipal(string accessToken)  
   {  
      var securityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_options.SecretKey));  
        
      var validation = new TokenValidationParameters  
      {  
         IssuerSigningKey = securityKey,  
         ValidateIssuer = false,  
         ValidateAudience = false,  
         ValidateLifetime = false,  
         ValidateIssuerSigningKey = true  
      };  
  
      return new JwtSecurityTokenHandler().ValidateToken(accessToken, validation, out _);  
   }  
  
}