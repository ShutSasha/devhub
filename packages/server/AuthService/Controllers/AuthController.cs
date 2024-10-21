using AuthService.Contracts.User;
using Microsoft.AspNetCore.Mvc;

namespace AuthService.Controllers;

[ApiController]
[Route("api/user")]
public class AuthController : ControllerBase
{
   private readonly Services.AuthService _authService;
   
   public AuthController(Services.AuthService authService)
   {
      _authService = authService;
   }

   [HttpPost("register")]
   public async Task<IActionResult> Register([FromBody] RegistrationUserRequest request)
   {
      try
      {
         await _authService.Register(request.UserName, request.Password, request.Email);
         return Ok();
      }
      catch (Exception e)
      {
         return BadRequest(new{message = e.Message});
      }
   }
   

   [HttpPost("verify-email/{email}")]
   public async Task<IActionResult> VerifyEmail([FromRoute] string email,[FromBody] VerifyEmailRequest request)
   {
      var result = await _authService.VerifyEmail(email, request.ActivationCode);

      if (result)
      {
         return Ok(new { message = "Email successfully verified" });
      }

      return BadRequest(new { message = "Invalid email or activation code" });
   }
}