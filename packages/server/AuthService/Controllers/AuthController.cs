using AuthService.Contracts.User;
using Microsoft.AspNetCore.Mvc;

namespace AuthService.Controllers;

[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
   private readonly Services.AuthService _authService;
   
   public AuthController(Services.AuthService authService)
   {
      _authService = authService;
   }

   [HttpPost("register")]
   // TODO: добавить repeatPassword
   public async Task<IActionResult> Register([FromBody] RegistrationUserRequest request)
   {
      try
      {
         if (!ModelState.IsValid)
         {
            return BadRequest(ModelState); 
         }
         var userResult = await _authService.Register(request.Username, request.Password, request.Email);
         return Ok(userResult);
      }
      catch (Exception e)
      {
         return BadRequest(new { message = e.Message });
      }
   }

   [HttpPost("login")]
   public async Task<IActionResult> Login([FromBody] LoginUserRequest request)
   {
      var loginResult = await _authService.Login(request.UserName, request.Password);
      HttpContext.Response.Cookies.Append("refreshToken",loginResult.RefreshToken, new CookieOptions()
      {
         HttpOnly = true,
         //TODO: На продакшн
         // Secure = true,
         SameSite = SameSiteMode.Strict,
         Expires = DateTime.UtcNow.AddDays(15)
      });
      return Ok(new {Token = loginResult.AccessToken, User = loginResult.UserData});
   }

   [HttpPost("verify-email")]
   public async Task<IActionResult> VerifyEmail([FromBody] VerifyEmailRequest request)
   {
      var result = await _authService.VerifyEmail(request.Email, request.ActivationCode);

      if (result)
      {
         return Ok(new { message = "Email successfully verified" });
      }

      return BadRequest(new { message = "Invalid email or activation code" });
   }

   [HttpPost("refresh")]
   public async Task<IActionResult> Refresh()
   {
      return Ok();
   }
}