using System.ComponentModel.DataAnnotations;
using ChatService.Helpers.Errors;

namespace ChatService.Contracts;

public class CreateChatRequest
{
   [Required]
   [ObjectIdValidation]
   public string UserId { get; set; }

   [Required]
   [ObjectIdValidation]
   public string TargetUserId { get; set;}
   
}