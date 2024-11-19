using System.ComponentModel.DataAnnotations;
using UserService.Helpers.Errors;

namespace UserService.Contracts.User;

public record EditUserRequest
{
   [ObjectIdValidation]
   public string Id { get; set; }
   public string Name { get; set; }
   [StringLength(200,ErrorMessage ="{0} length must be less than {1}") ]
   public string Bio { get; set; }
}