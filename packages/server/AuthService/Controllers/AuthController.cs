using AuthService.Contracts.Email;
using AuthService.Contracts.User;
using AuthService.Helpers.Response;
using AuthService.Helpers.ThirdParty;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;

namespace AuthService.Controllers;

[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
   private readonly Services.AuthService _authService;
   private readonly GoogleAuthOptions _googleAuthOptions;

   public AuthController(Services.AuthService authService, IOptions<GoogleAuthOptions> googleAuthOptions)
   {
      _authService = authService;
      _googleAuthOptions = googleAuthOptions.Value;
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
         return BadRequest(new
         {
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

         return Ok(new
            { Message = "Tokens updated", Token = refreshResult.AccessToken, User = refreshResult.UserData });
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

   [HttpPatch("password-verification-code")]
   public async Task<IActionResult> SendVerificationCode([FromBody] SendVerificationCodeRequest request)
   {
      try
      {
         await _authService.SendVerificationCode(request.Email);
         return Ok(new { Message = "Verification code has been sent to your email." });
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(400, "Send Verification error", e.Message);
      }
   }

   [HttpPatch("change-password")]
   public async Task<IActionResult> ChangePassword([FromBody] ChangePasswordRequest request)
   {
      try
      {
         await _authService.ChangePassword(request.Email, request.Password);
         return Ok(new { Message = "Password updated successfully" });
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(400, "Change password error", e.Message);
      }
   }

   [HttpPost("logout")]
   public async Task<IActionResult> Logout()
   {
      try
      {
         HttpContext.Response.Cookies.Delete("refreshToken");
         return Ok(new { Message = "Successfully logout" });
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(500, "Logout error", "Something went wrong");
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
            { "Fetch Error", new List<string> { "Cannot fetch" } }
         }
      });
   }

   [HttpGet("google-login")]
   public async Task<IActionResult> GoogleLogin()
   {
      var authorizationUrl = $"https://accounts.google.com/o/oauth2/v2/auth?" +
                             $"client_id={_googleAuthOptions.ClientId}" +
                             $"&response_type=code" +
                             $"&scope=email%20profile" +
                             $"&redirect_uri={_googleAuthOptions.RedirectionUri}" +
                             $"&access_type=offline";

      return Redirect(authorizationUrl);
   }


   [HttpGet("signin-google")]
   public async Task<IActionResult> GoogleCallback(string code)
   {
      if (string.IsNullOrEmpty(code))
      {
         return BadRequest("Authorization code not provided");
      }

      try
      {
         var tokenResponse = await _authService.ExchangeCodeForTokensAsync(code);
         var userInfo = await _authService.GetGoogleUserInfoAsync(tokenResponse.access_token);

         var userResult = await _authService.GoogleSignInOrSignUp(userInfo);

         return Ok(new { Token = userResult.AccessToken, User = userResult.UserData });
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(400, "Google auth error", e.Message);
      }
   }
}