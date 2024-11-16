using Amazon.Util.Internal;
using Microsoft.AspNetCore.Mvc;
using UserService.Abstracts;
using UserService.Contracts.User;
using UserService.Helpers.Response;
using ZstdSharp.Unsafe;

namespace UserService.Controllers;

[ApiController]
[Route("api/users")]
public class UserController : ControllerBase
{
   private readonly IUserService _userService;

   public UserController(IUserService userService)
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

   [HttpPost("update-photo/{userId}")]
   public async Task<IActionResult> UpdateUserPhoto(IFormFile file, [FromRoute] string userId)
   {
      if (file == null || file.Length == 0)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            400,
            "File upload Exception",
            "File is empty or wasn't send");
      }

      try
      {
         await using var fileStream = file.OpenReadStream();
         var fileName = file.FileName;
         var contentType = file.ContentType;
         await _userService.EditUserIcon(userId, fileName, fileStream, contentType);

         return Ok(new { Message = "User photo updated successfully." });
      }
      catch (Exception ex)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(ex.Message.Split(":")[0]),
            "Update user error",
            ex.Message.Split(":")[1]);
      }
   }
}