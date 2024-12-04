using AuthService.Contracts.Email;
using AuthService.Contracts.User;
using AuthService.Helpers.Response;
using AuthService.Helpers.ThirdParty;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authorization;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using MongoDB.Bson;
using MongoDB.Bson.IO;
using Newtonsoft.Json;
using Swashbuckle.AspNetCore.Annotations;
using JsonConvert = Newtonsoft.Json.JsonConvert;

namespace AuthService.Controllers;

[ApiController]
[Route("api/auth")]
public class AuthController : ControllerBase
{
   private readonly Services.AuthService _authService;
   private readonly IConfiguration _configuration;
   private readonly GoogleAuthOptions _googleAuthOptions;

   public AuthController(Services.AuthService authService, IOptions<GoogleAuthOptions> googleAuthOptions, IConfiguration configuration)
   {
      _authService = authService;
      _configuration = configuration;
      _googleAuthOptions = googleAuthOptions.Value;
   }

   [HttpPost("register")]
   [SwaggerOperation("Register user")]
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
         return ErrorResponseHelper.CreateErrorResponse(400, $"{nameof(Register)} error", e.Message);
      }
   }

   [HttpPost("login")]
   [SwaggerOperation("Authorize user")]
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
            SameSite = SameSiteMode.None,
            Expires = DateTime.Now.AddSeconds(30)
         });
         return Ok(new { AccessToken = loginResult.AccessToken, RefreshToken = loginResult.RefreshToken, User = loginResult.UserData });
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(400, "Login error", e.Message);
      }
   }

   [HttpPost("verify-email")]
   [SwaggerOperation("Verify user email address")]
   public async Task<IActionResult> VerifyEmail([FromBody] VerifyEmailRequest request)
   {
      try
      {
         await _authService.VerifyEmail(request.Email, request.ActivationCode);
         return Ok(new { message = "Email successfully verified" });
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(400, "Activation code error", e.Message);
      }
   }

   [HttpPost("refresh")]
   [SwaggerOperation("Refresh user access and refresh tokens")]
   public async Task<IActionResult> Refresh()
   {
      try
      {
         var refreshToken = HttpContext.Request.Headers["X-Refresh-Token"].FirstOrDefault();
        
         if (string.IsNullOrEmpty(refreshToken))
         {
            refreshToken = HttpContext.Request.Cookies["refreshToken"];
         }

         var refreshResult = await _authService.RefreshTokens(refreshToken);

         HttpContext.Response.Cookies.Append("refreshToken", refreshResult.RefreshToken, new CookieOptions()
         {
            HttpOnly = true,
            // Secure = true,
            SameSite = SameSiteMode.None,
            Expires = DateTime.UtcNow.AddDays(30)
         });

         return Ok(new { Message = "Tokens updated", AccessToken = refreshResult.AccessToken, RefreshToken = refreshResult.RefreshToken,  User = refreshResult.UserData });
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(401, "Refresh error", e.Message);
      }
   }

   [HttpPatch("password-verification-code")]
   [SwaggerOperation("Send verification code for password recovering")]
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
   [SwaggerOperation("Update user password")]
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
   [SwaggerOperation("Unauthorize user")]
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
   [SwaggerIgnore]
   public async Task<IActionResult> GetInformation([FromServices] IHttpClientFactory httpClientFactory)
   {
      var httpClient = httpClientFactory.CreateClient();
      var response = await httpClient.GetAsync("https://jsonplaceholder.typicode.com/posts/1");

      if (response.IsSuccessStatusCode)
      {
         var data = await response.Content.ReadFromJsonAsync<object>();
         return Ok(data);
      }

      return ErrorResponseHelper.CreateErrorResponse(401, "Fetch error", "Can't fetch");
   }
   
   [HttpGet("google-login")]
   [SwaggerIgnore]
   public async Task<IActionResult> GoogleLogin()
   {
      try
      {
         var redirectUrl = Url.Action(nameof(GoogleCallback), "Auth",null,Request.Scheme);
         
         var properties = new AuthenticationProperties { RedirectUri = redirectUrl };

         return Challenge(properties,"google");
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            400, 
            "Google auth error",
            "Error while redirect callback");
      }
   }
   

   [HttpGet("signin-google")]
   [SwaggerIgnore]
   public async Task<IActionResult> GoogleCallback()
   {
      var authenticateResult = await HttpContext.AuthenticateAsync("google");

      if (!authenticateResult.Succeeded)
      {
         return BadRequest("Auth error");
      }
      
      var claims = authenticateResult.Principal?.Identities.FirstOrDefault()?.Claims;
      var userInfo = new
      {
         Name = claims?.FirstOrDefault(c => c.Type == "name")?.Value,
         Email = claims?.FirstOrDefault(c => c.Type == "email")?.Value,
         Avatar = claims?.FirstOrDefault(c => c.Type == "avatar_url")?.Value,
      }.ToJson();

      var user = JsonConvert.DeserializeObject<UserInfo>(userInfo);

      var userResult = await _authService.SignInOrSignUp(user);

      HttpContext.Response.Cookies.Append("refreshToken", userResult.RefreshToken, new CookieOptions
      {
         HttpOnly = true,
         Secure = true, 
         SameSite = SameSiteMode.Strict 
      });

      return Redirect("http://localhost:3000");
   }
   
   [HttpGet("github-login")]
   [SwaggerIgnore]
   public async Task<IActionResult> GitHubLogin()
   {
      try
      {
         var redirectUrl = Url.Action(nameof(GitHubCallback), "Auth",null,Request.Scheme);
         
         var properties = new AuthenticationProperties { RedirectUri = redirectUrl };

         return Challenge(properties,"github");
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(400, "GitHub auth error", "Error while redirect callback");
      }
   }
   
   [HttpGet("signin-github")]
   [SwaggerIgnore]
   public async Task<IActionResult> GitHubCallback()
   {
      var authenticateResult = await HttpContext.AuthenticateAsync("github");

      if (!authenticateResult.Succeeded)
      {
         return BadRequest("Auth error");
      }
      
      var claims = authenticateResult.Principal?.Identities.FirstOrDefault()?.Claims;
      var userInfo = new
      {
         Name = claims?.FirstOrDefault(c => c.Type == "name")?.Value,
         Email = claims?.FirstOrDefault(c => c.Type == "email")?.Value,
         Avatar = claims?.FirstOrDefault(c => c.Type == "avatar_url")?.Value,
      }.ToJson();

      var user = JsonConvert.DeserializeObject<UserInfo>(userInfo);

      var userResult = await _authService.SignInOrSignUp(user);

      HttpContext.Response.Cookies.Append("refreshToken", userResult.RefreshToken, new CookieOptions
      {
         HttpOnly = true,
         Secure = true, 
         SameSite = SameSiteMode.Strict 
      });

      //return Ok(new { AccessToken = userResult.AccessToken, RefreshToken = userResult.RefreshToken, UserData = userResult.UserData});
      return Redirect("http://localhost:3000");
   }
}