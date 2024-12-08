using Microsoft.AspNetCore.Mvc;

namespace ApiGetaway.Helpers.Response;

public class ErrorResponseHelper
{
   public static IActionResult CreateErrorResponse(int statusCode, string errorKey, string errorMessage)
   {
      return new ObjectResult(new
      {
         status = statusCode,
         errors = new Dictionary<string, List<string>>
         {
            { errorKey, new List<string> { errorMessage } }
         }
      })
      {
         StatusCode = statusCode
      };
   }
}