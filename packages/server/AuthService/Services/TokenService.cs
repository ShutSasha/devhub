using System.Security.Cryptography;
using AuthService.Helpers.Jwt;

namespace AuthService.Services;

public class TokenService
{
   private readonly JwtProvider _jwtProvider;

   public TokenService(JwtProvider jwtProvider)
   {
      _jwtProvider = jwtProvider;
   }
   
   public string GenerateRefreshTokenString()
   {
      var randomNumbers = new byte[64];

      using (var randomNumberGenerator = RandomNumberGenerator.Create())
      {
         randomNumberGenerator.GetBytes(randomNumbers);
      }

      return Convert.ToBase64String(randomNumbers);
   }
   
   
}