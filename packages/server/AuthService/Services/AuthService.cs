using AuthService.Contracts.User;
using AuthService.Helpers.Password;
using AuthService.Models;
using AuthService.Models.Enums;
using Microsoft.AspNetCore.Server.Kestrel.Transport.Quic;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

namespace AuthService.Services;

public class AuthService
{
   private readonly IMongoCollection<User> _userCollection;
   private readonly PasswordHasher _passwordHasher;
   private readonly MailService _mailService;

   public AuthService(IMongoDatabase mongoDatabase, IOptions<MongoDbSettings> mongoDbSettings,
      PasswordHasher passwordHasher, MailService mailService)
   {
      _userCollection = mongoDatabase.GetCollection<User>(mongoDbSettings.Value.CollectionName);
      _passwordHasher = passwordHasher;
      _mailService = mailService;
   }

   public async Task Register(string username, string password, string email)
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
               return;
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