using System.Net.Http.Headers;
using AuthService.Contracts.Token;
using AuthService.Contracts.User;
using AuthService.Dtos;
using AuthService.Helpers.Jwt;
using AuthService.Helpers.Security;
using AuthService.Helpers.ThirdParty;
using AuthService.Models;
using AuthService.Models.Enums;
using AutoMapper;
using Microsoft.AspNetCore.Server.Kestrel.Transport.Quic;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using Newtonsoft.Json;

namespace AuthService.Services;

public class AuthService
{
   private readonly IMongoCollection<User> _userCollection;
   private readonly PasswordHasher _passwordHasher;
   private readonly MailService _mailService;
   private readonly JwtProvider _jwtProvider;
   private readonly TokenService _tokenService;
   private readonly IMapper _mapper;
   private readonly GoogleAuthOptions _googleAuthOptions;

   public AuthService(IMongoDatabase mongoDatabase, IOptions<MongoDbSettings> mongoDbSettings,
      PasswordHasher passwordHasher, MailService mailService, JwtProvider jwtProvider,
      TokenService tokenService, IMapper mapper, IOptions<GoogleAuthOptions> googleAuthOptions)
   {
      _userCollection = mongoDatabase.GetCollection<User>(mongoDbSettings.Value.CollectionName);
      _passwordHasher = passwordHasher;
      _mailService = mailService;
      _jwtProvider = jwtProvider;
      _tokenService = tokenService;
      _mapper = mapper;
      _googleAuthOptions = googleAuthOptions.Value;
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
         Avatar =
            "https://upload.wikimedia.org/wikipedia/commons/thumb/5/59/User-avatar.svg/2048px-User-avatar.svg.png",
      };

      await _userCollection.InsertOneAsync(user);
      await _mailService.SendVerificationCode(email, newCode);
      return user;
   }

   public async Task<LoginUserResponse> Login(string userName, string password)
   {
      var loginResponse = new LoginUserResponse();

      var candidate = await _userCollection.Find(u => u.UserName == userName).FirstOrDefaultAsync();

      if (candidate == null)
      {
         throw new Exception("User wasn't found");
      }

      if (!candidate.IsActivated)
      {
         throw new Exception("User account isn't activated");
      }
      
      var passwordVerifyResult = _passwordHasher.VerifyPassword(password, candidate.Password);

      if (!passwordVerifyResult)
      {
         throw new Exception("Failed to login. Incorrect password");
      }

      var (accessToken, refreshToken) = await _tokenService.GenerateTokens(candidate);

      loginResponse.AccessToken = accessToken;
      loginResponse.RefreshToken = refreshToken;
      loginResponse.UserData = _mapper.Map<UserDto>(candidate);

      return loginResponse;
   }

   public async Task<LoginUserResponse> RefreshTokens(string refreshToken)
   {
      var loginResponse = new LoginUserResponse();

      if (string.IsNullOrEmpty(refreshToken))
      {
         throw new Exception("Empty token");
      }

      var principal = _jwtProvider.GetPrincipal(refreshToken);
      if (principal == null || principal.Claims.All(c => c.Type == "Id"))
      {
         throw new Exception("Invalid refresh token");
      }

      var userId = principal.Claims.FirstOrDefault(c => c.Type == "Id")?.Value;

      if (string.IsNullOrEmpty(userId))
      {
         throw new Exception("Invalid user id");
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

   public async Task VerifyEmail(string email, string activationCode)
   {
      var user = await _userCollection.Find(u => u.Email == email).FirstOrDefaultAsync();

      if (user == null || user.ActivationCode != activationCode)
      {
         throw new Exception("Invalid email or password");
      }

      user.IsActivated = true;
      user.ActivationCode = null;

      await _userCollection.ReplaceOneAsync(u => u.Id == user.Id, user);
   }

   public async Task SendVerificationCode(string email)
   {
      var user = await _userCollection.Find(u => u.Email == email).FirstOrDefaultAsync();

      if (user == null)
      {
         throw new Exception("User with this email wasn't found");
      }

      var verificationCode = GenerateActivationCode();

      var update = Builders<User>.Update.Set(u => u.ActivationCode, verificationCode);

      await _userCollection.UpdateOneAsync(
         u => u.Email == email,
         update
      );

      await _mailService.SendVerificationCode(user.Email, verificationCode);
   }

   public async Task ChangePassword(string email, string password)
   {
      var user = await _userCollection.Find(u => u.Email == email).FirstOrDefaultAsync();

      if (user == null)
      {
         throw new Exception("User wasn't found");
      }

      var hashedPassword = _passwordHasher.GenerateHash(password);

      var update = Builders<User>.Update.Combine(
         Builders<User>.Update.Set(u => u.Password, hashedPassword),
         Builders<User>.Update.Set(u => u.ActivationCode, null)
      );

      await _userCollection.UpdateOneAsync(u => u.Email == email, update);
   }

   public async Task<TokenResponse> ExchangeCodeForTokensAsync(string code)
   {
      var tokenRequest = new HttpRequestMessage(HttpMethod.Post, "https://oauth2.googleapis.com/token");

      var parameters = new Dictionary<string, string>
      {
         { "code", code },
         { "client_id", _googleAuthOptions.ClientId },
         { "client_secret", _googleAuthOptions.ClientSecret },
         { "redirect_uri", "http://localhost:5279/api/auth/signin-google" },
         { "grant_type", "authorization_code" }
      };

      tokenRequest.Content = new FormUrlEncodedContent(parameters);

      using var httpClient = new HttpClient();
      var response = await httpClient.SendAsync(tokenRequest);

      if (!response.IsSuccessStatusCode)
      {
         var errorContent = await response.Content.ReadAsStringAsync();
         throw new Exception($"Error exchanging code for tokens: {errorContent}");
      }

      var jsonResponse = await response.Content.ReadAsStringAsync();
      return JsonConvert.DeserializeObject<TokenResponse>(jsonResponse);
   }

   public async Task<UserInfo> GetGoogleUserInfoAsync(string accessToken)
   {
      using var httpClient = new HttpClient();
      httpClient.DefaultRequestHeaders.Authorization = new AuthenticationHeaderValue("Bearer", accessToken);

      var response = await httpClient.GetAsync("https://www.googleapis.com/oauth2/v3/userinfo");

      if (!response.IsSuccessStatusCode)
      {
         var errorContent = await response.Content.ReadAsStringAsync();
         throw new Exception($"Error retrieving user info: {errorContent}");
      }

      var jsonResponse = await response.Content.ReadAsStringAsync();
      return JsonConvert.DeserializeObject<UserInfo>(jsonResponse)!;
   }

   public async Task<LoginUserResponse> SignInOrSignUp(UserInfo userInfo)
   {
      var existingUser = await _userCollection.Find(u => u.Email == userInfo.email).FirstOrDefaultAsync();

      if (existingUser != null)
      {
         var (accessToken, refreshToken) = await _tokenService.GenerateTokens(existingUser);

         return new LoginUserResponse
         {
            AccessToken = accessToken,
            RefreshToken = refreshToken,
            UserData = _mapper.Map<UserDto>(existingUser)
         };
      }
      else
      {
         var newUser = new User
         {
            UserName = userInfo.name,
            Email = userInfo.email,
            Avatar = userInfo.picture ?? userInfo.avatar,
            IsActivated = true,
            Password = null
         };

         await _userCollection.InsertOneAsync(newUser);

         var (newAccessToken, newRefreshToken) = await _tokenService.GenerateTokens(newUser);

         return new LoginUserResponse
         {
            AccessToken = newAccessToken,
            RefreshToken = newRefreshToken,
            UserData = _mapper.Map<UserDto>(newUser)
         };
      }
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