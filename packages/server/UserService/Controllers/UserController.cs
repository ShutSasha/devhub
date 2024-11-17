using Microsoft.AspNetCore.Mvc;
using UserService.Contracts.User;
using UserService.Helpers.Response;

namespace UserService.Controllers;

[ApiController]
[Route("api/users")]
public class UserController : ControllerBase
{
   private readonly Services.UserService _userService;

   public UserController(Services.UserService userService)
   {
      _userService = userService;
   }

   [HttpPatch]
   public async Task<IActionResult> EditUser([FromBody] EditUserRequest request)
   {
      try
      {
         await _userService.EditUser(request.Id, request.Name, request.Bio, request.Tags);
         return StatusCode(200, new { Message = "Successfully updated" });
      }
      catch (Exception e)
      {
         int statusCode = Convert.ToInt32(e.Message.Split(":")[0]);
         return ErrorResponseHelper.CreateErrorResponse(
            statusCode,
            "Edit user error",
            e.Message.Split(":")[1]
         );
      }
   }
}