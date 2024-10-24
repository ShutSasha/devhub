using System.ComponentModel.DataAnnotations;  
  
namespace AuthService.Contracts.User;  
  
public record LoginUserRequest(  
   [Required] string UserName,  
   [Required] string Password  
);