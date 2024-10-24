using System.Security.Cryptography;
using AuthService.Helpers.Jwt;
using AuthService.Models;

namespace AuthService.Services;

public class TokenService
{
   private readonly JwtProvider _jwtProvider;

   public TokenService(JwtProvider jwtProvider)
   {
      _jwtProvider = jwtProvider;
   }

   public Task<Tuple<string,string>> GenerateTokens(User user)
   {
      var accessToken = _jwtProvider.GenerateAccessToken(user);
      var refreshToken = _jwtProvider.GenerateRefreshToken(user);

      return Task.FromResult(Tuple.Create(accessToken, refreshToken));
   }
   
   
}