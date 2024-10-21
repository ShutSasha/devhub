using BCrypt.Net;
namespace AuthService.Helpers.Password;

public class PasswordHasher
{
   public string GenerateHash(string password)
      => BCrypt.Net.BCrypt.EnhancedHashPassword(password, HashType.SHA256);

   public bool VerifyPassword(string password, string hashedPassword)
      => BCrypt.Net.BCrypt.EnhancedVerify(password,hashedPassword,HashType.SHA256);
}