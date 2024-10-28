using AuthService.Contracts.User;
using Microsoft.AspNetCore.Authorization;
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
         return BadRequest(new {
            status = 400,
            errors = new Dictionary<string, List<string>>
            {
               { "Registration error", new List<string> { e.Message } }
            }
         });
      }
   }

   [HttpPost("login")]
   public async Task<IActionResult> Login([FromBody] LoginUserRequest request)
   {
      try
      {
         var loginResult = await _authService.Login(request.Username, request.Password);
         HttpContext.Response.Cookies.Append("refreshToken", loginResult.RefreshToken, new CookieOptions()
         {
            HttpOnly = true,
            //TODO: Сделать проверку на environment mode 
            // Secure = true,
            SameSite = SameSiteMode.Strict,
            Expires = DateTime.Now.AddSeconds(30)
         });
         return Ok(new { Token = loginResult.AccessToken, User = loginResult.UserData });
      }
      catch (Exception e)
      {
         return BadRequest(new
         {
            status = 400,
            errors = new Dictionary<string, List<string>>
            {
               { "Login error", new List<string> { e.Message } }
            }
         });
      }
   }

   [HttpPost("verify-email")]
   public async Task<IActionResult> VerifyEmail([FromBody] VerifyEmailRequest request)
   {
      var result = await _authService.VerifyEmail(request.Email, request.ActivationCode);

      if (result)
      {
         return Ok(new { message = "Email successfully verified" });
      }

      return BadRequest(new
      {
         status = 400,
         errors = new Dictionary<string, List<string>>
         {
            { "ActivationCode", new List<string> { "Invalid email or activation code" } }
         }
      });
   }

   [HttpPost("refresh")]
   public async Task<IActionResult> Refresh()
   {
      try
      {
         var refreshToken = HttpContext.Request.Cookies["refreshToken"];

         var refreshResult = await _authService.RefreshTokens(refreshToken);

         HttpContext.Response.Cookies.Append("refreshToken", refreshResult.RefreshToken, new CookieOptions()
         {
            HttpOnly = true,
            // Secure = true,
            SameSite = SameSiteMode.Strict,
            Expires = DateTime.UtcNow.AddDays(30)
         });

         return Ok(new { Message = "Tokens updated", Token = refreshResult.AccessToken });
      }
      catch (Exception e)
      {
         return Unauthorized(new
         {
            status = 401,
            errors = new Dictionary<string, List<string>>
            {
               { "Refresh error", new List<string> { e.Message } }
            }
         });
      }
   }

   [Authorize]
   [HttpGet("testinfo")]
   public async Task<IActionResult> GetInformation([FromServices] IHttpClientFactory httpClientFactory)
   {
      var httpClient = httpClientFactory.CreateClient();
      var response = await httpClient.GetAsync("https://jsonplaceholder.typicode.com/posts/1");

      if (response.IsSuccessStatusCode)
      {
         var data = await response.Content.ReadFromJsonAsync<object>();
         return Ok(data);
      }

      return Unauthorized(new
      {
         status = 401,
         errors = new Dictionary<string, List<string>>
         {
            { "Fetch Error", new List<string> {"Cannot fetch"} }
         }
      });
   }
}