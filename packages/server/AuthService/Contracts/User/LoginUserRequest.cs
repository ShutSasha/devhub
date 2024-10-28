using System.ComponentModel.DataAnnotations;  
  
namespace AuthService.Contracts.User;  
  
public record LoginUserRequest(  
   [Required] string Username,  
   [Required] string Password  
);