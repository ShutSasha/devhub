using System.ComponentModel.DataAnnotations;
using ChatService.Helpers.Errors;

namespace ChatService.Contracts;

public class AddMessageRequest
{
   [ObjectIdValidation]
   public string ChatId { get; set; }

   [ObjectIdValidation]
   public string UserId { get; set; }
   
   public string Content { get; set; }
}