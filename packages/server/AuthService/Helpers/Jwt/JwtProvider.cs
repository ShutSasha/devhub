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
  
   public string GenerateAccessToken(User user)
   {
      Claim[] claims = new[]
      {
         new Claim("Id", user.Id),
         new Claim("type", "access"),
         new Claim("Email", user.Email),

      }; 
      var signingCredentials = new SigningCredentials(  
         new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_options.AccessSecretKey)),  
         SecurityAlgorithms.HmacSha256);

      var accessToken = new JwtSecurityToken(
         claims: claims,
         signingCredentials: signingCredentials,
         expires: DateTime.Now.AddMinutes(_options.AccessExpiresDuration));
  
      var tokenValue = new JwtSecurityTokenHandler().WriteToken(accessToken);  
  
      return tokenValue;  
   }
   
   public string GenerateRefreshToken(User user)
   {
      Claim[] claims = new[]
      {
         new Claim("Id", user.Id),
         new Claim("type", "refresh"),
         new Claim("Email", user.Email),
      };

      var signingCredentials = new SigningCredentials(
         new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_options.RefreshSecretKey)),
         SecurityAlgorithms.HmacSha256);

      var refreshToken = new JwtSecurityToken(
         claims: claims,
         signingCredentials: signingCredentials,
         expires: DateTime.Now.AddDays(_options.RefreshExpiresDuration));

      return new JwtSecurityTokenHandler().WriteToken(refreshToken);
   }
  
   public ClaimsPrincipal GetPrincipal(string refreshToken)  
   {  
      var securityKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(_options.RefreshSecretKey));  
        
      var validation = new TokenValidationParameters  
      {  
         IssuerSigningKey = securityKey,  
         ValidateIssuer = false,  
         ValidateAudience = false,  
         ValidateLifetime = false,  
         ValidateIssuerSigningKey = true  
      };  
  
      return new JwtSecurityTokenHandler().ValidateToken(refreshToken, validation, out _);  
   }  
  
}