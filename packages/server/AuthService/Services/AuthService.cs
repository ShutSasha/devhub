using AuthService.Contracts.Token;
using AuthService.Contracts.User;
using AuthService.Dtos;
using AuthService.Helpers.Jwt;
using AuthService.Helpers.Password;
using AuthService.Models;
using AuthService.Models.Enums;
using AutoMapper;
using Microsoft.AspNetCore.Server.Kestrel.Transport.Quic;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

namespace AuthService.Services;

public class AuthService
{
   private readonly IMongoCollection<User> _userCollection;
   private readonly PasswordHasher _passwordHasher;
   private readonly MailService _mailService;
   private readonly JwtProvider _jwtProvider;
   private readonly TokenService _tokenService;
   private readonly IMapper _mapper;

   public AuthService(IMongoDatabase mongoDatabase, IOptions<MongoDbSettings> mongoDbSettings,
      PasswordHasher passwordHasher, MailService mailService, JwtProvider jwtProvider,
      TokenService tokenService,IMapper mapper)
   {
      _userCollection = mongoDatabase.GetCollection<User>(mongoDbSettings.Value.CollectionName);
      _passwordHasher = passwordHasher;
      _mailService = mailService;
      _jwtProvider = jwtProvider;
      _tokenService = tokenService;
      _mapper = mapper;
   }

   public async Task<User> Register(string username, string password, string email)
   {
      var candidate = await _userCollection.FindAsync(
         Builders<User>.Filter.Or(
            Builders<User>.Filter.Eq(u => u.Email, email),
            Builders<User>.Filter.Eq(u => u.UserName, username)
         )
      );
      
      var existingUser = await candidate.FirstOrDefaultAsync();

      if (existingUser != null)
      {
         switch (existingUser.IsActivated)
         {
            case false:
            {
               var code = GenerateActivationCode();
               existingUser.ActivationCode = code;
               
               var update = Builders<User>.Update.Set(u => u.ActivationCode, code);
               await _userCollection.UpdateOneAsync(
                  Builders<User>.Filter.Eq(u => u.Email, email), update
               );
               
               await _mailService.SendVerificationCode(email, code);
               return existingUser;
            }
            case true:
            {
               throw new Exception("User with this email or username already exists.");
            }
         }
      }
      
      var passwordHash = _passwordHasher.GenerateHash(password);
      var newCode = GenerateActivationCode();
      
      var user = new User()
      {
         UserName = username,
         Password = passwordHash,
         Email = email,
         ActivationCode = newCode,
         Avatar = "https://upload.wikimedia.org/wikipedia/commons/thumb/5/59/User-avatar.svg/2048px-User-avatar.svg.png",
      };
      
      await _userCollection.InsertOneAsync(user);
      await _mailService.SendVerificationCode(email,newCode);
      return user;
   }


   public async Task<LoginUserResponse> Login(string userName,string password)
   {
      var loginResponse = new LoginUserResponse();

      var candidate = await _userCollection.Find(u => u.UserName == userName).FirstOrDefaultAsync();

      if (candidate == null)
      {
         throw new Exception("User wasn't found");
      }

      var passwordVerifyResult = _passwordHasher.VerifyPassword(password, candidate.Password);

      if (!passwordVerifyResult)
      {
         throw new Exception("Failed to login. Incorrect password");
      }

      var (accessToken,refreshToken) = await _tokenService.GenerateTokens(candidate);
      
      loginResponse.AccessToken = accessToken;
      loginResponse.RefreshToken = refreshToken;
      loginResponse.UserData = _mapper.Map<UserDto>(candidate);
      
      return loginResponse;
      
   }
   public async Task<LoginUserResponse> Refresh(string refreshToken)
   {
      var loginResponse = new LoginUserResponse();

      if (string.IsNullOrEmpty(refreshToken))
      {
         return loginResponse; 
      }
      
      var principal = _jwtProvider.GetPrincipal(refreshToken);
      if (principal == null || principal.Claims.All(c => c.Type == "Id"))
      {
         throw new Exception("Invalid refresh token");
      }

      var userId = principal.Claims.FirstOrDefault(c => c.Type == "Id")?.Value;

      if (string.IsNullOrEmpty(userId))
      {
         return loginResponse; 
      }
      
      var user = await _userCollection.Find(u => u.Id == userId).FirstOrDefaultAsync();
      if (user == null)
      {
         throw new Exception("Unauthorized user");
      }

      var (newAccessToken, newRefreshToken) = await _tokenService.GenerateTokens(user);

      loginResponse.AccessToken = newAccessToken;
      loginResponse.RefreshToken = newRefreshToken;
      loginResponse.UserData = _mapper.Map<UserDto>(user);
      return loginResponse;
   }

   
   public async Task<bool> VerifyEmail(string email, string activationCode)
   {
      var user = await _userCollection.Find(u => u.Email == email).FirstOrDefaultAsync();
      
      if (user == null || user.ActivationCode != activationCode)
      {
         return false;
      }
      
      user.IsActivated = true; 
      user.ActivationCode = null; 
      
      await _userCollection.ReplaceOneAsync(u => u.Id == user.Id, user);
      
      return true; 
   }
   
   private string GenerateActivationCode()
   {
      const int length = 6;
      var random = new Random();
      var code = new char[length];
    
      for (int i = 0; i < length; i++)
      {
         code[i] = (char)('0' + random.Next(0, 10));
      }

      return new string(code);
   }
}