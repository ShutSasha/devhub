using System.ComponentModel.DataAnnotations;
using UserService.Helpers.Errors;

namespace UserService.Contracts.Report;

public class CreateReportRequest
{
   [Required]
   [ObjectIdValidation]
   public string Sender { get; set; }
   
   [Required]
   [ObjectIdValidation]
   public string Content { get; set; }
   
   [Required]
   public string Category { get; set; }
}