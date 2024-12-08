using System.ComponentModel.DataAnnotations;
using UserService.Helpers.Errors;

namespace UserService.Contracts.User;

public record UserSavedPostRequest
{
   [Required]
   [ObjectIdValidation]
   public string UserId { get; set; }

   [Required]
   [ObjectIdValidation]
   public string SavedPostId { get; set; }
}