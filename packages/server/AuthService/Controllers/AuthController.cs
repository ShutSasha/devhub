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
         return Ok(new { AccessToken = loginResult.AccessToken, RefreshToken = loginResult.RefreshToken, User = loginResult.UserData });
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(400, "Login error", e.Message);
      }
   }

   [HttpPost("verify-email")]
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
            SameSite = SameSiteMode.Strict,
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

      return ErrorResponseHelper.CreateErrorResponse(401, "Fetch error", "Can't fetch");
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

         var userResult = await _authService.SignInOrSignUp(userInfo);
         
         HttpContext.Response.Cookies.Append("refreshToken", userResult.RefreshToken);
         return Ok(new { AccessToken = userResult.AccessToken, RefreshToken = userResult.RefreshToken, User = userResult.UserData });
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(400, "Google auth error", e.Message);
      }
   }
   
   [HttpGet("github-login")]
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

      HttpContext.Response.Cookies.Append("refreshToken", userResult.RefreshToken);
      
      return Ok(new { AccessToken = userResult.AccessToken, RefreshToken = userResult.RefreshToken, User = userResult.UserData });
   }
}