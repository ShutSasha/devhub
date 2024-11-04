using System.Collections;
using System.Diagnostics;
using System.Net.Http.Headers;
using System.Security.Claims;
using System.Text;
using System.Text.Json;
using AuthService.Helpers.Jwt;
using AuthService.Helpers.Response;
using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.AspNetCore.Authentication.OAuth;
using Microsoft.IdentityModel.Tokens;
using Newtonsoft.Json;
using JsonSerializer = System.Text.Json.JsonSerializer;
using PathString = Microsoft.AspNetCore.Http.PathString;

namespace AuthService.Extensions
{
   public static class ApiExtension
   {
      public static void AddApiAuthentication(this IServiceCollection services, IConfiguration configuration)
      {
         var jwtOptions = new JwtOptions();
         configuration.GetSection(nameof(JwtOptions)).Bind(jwtOptions);
         services.Configure<JwtOptions>(configuration.GetSection(nameof(JwtOptions)));

         services.AddAuthentication(options =>
            {
               options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
               options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
               options.DefaultScheme = JwtBearerDefaults.AuthenticationScheme;
               options.DefaultAuthenticateScheme = CookieAuthenticationDefaults.AuthenticationScheme;
               options.DefaultChallengeScheme = "google";
               options.DefaultChallengeScheme = "github";
            })
            .AddJwtBearer(JwtBearerDefaults.AuthenticationScheme, options =>
            {
               options.TokenValidationParameters = new TokenValidationParameters
               {
                  ValidateIssuer = false,
                  ValidateAudience = false,
                  ValidateLifetime = true,
                  ValidateIssuerSigningKey = true,
                  ClockSkew = TimeSpan.Zero,
                  IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtOptions.AccessSecretKey))
               };

               options.Events = new JwtBearerEvents
               {
                  OnMessageReceived = context =>
                  {
                     var authorizationHeader = context.Request.Headers["Authorization"].ToString();
                     if (!string.IsNullOrEmpty(authorizationHeader) &&
                         authorizationHeader.StartsWith("Bearer "))
                     {
                        context.Token = authorizationHeader.Substring("Bearer ".Length).Trim();
                     }
                     else
                     {
                        context.Token = context.Request.Cookies["refreshToken"];

                        if (string.IsNullOrEmpty(context.Token))
                        {
                           context.Token = context.Request.Headers["X-Refresh-Token"];
                        }
                     }

                     return Task.CompletedTask;
                  },

                  OnChallenge = context =>
                  {
                     context.HandleResponse();
                     context.Response.StatusCode = StatusCodes.Status401Unauthorized;
                     context.Response.ContentType = "application/json";
                     var response = ErrorResponseHelper.CreateErrorResponse(
                        401,
                        "Auth error",
                        "User is unauthorized");

                     return context.Response.WriteAsync(JsonConvert.SerializeObject(response));
                  }
               };
            })
            .AddCookie(options =>
            {
               options.Cookie.SameSite = SameSiteMode.None;
               options.Cookie.SecurePolicy = CookieSecurePolicy.Always;
            })
            .AddOAuth("github", options =>
            {
               options.SignInScheme = CookieAuthenticationDefaults.AuthenticationScheme;
               options.ClientId = configuration["GitHubOAuth:ClientId"]!;
               options.ClientSecret = configuration["GitHubOAuth:ClientSecret"]!;
               options.CallbackPath = configuration["GitHubOAuth:CallbackUri"];
               options.AuthorizationEndpoint = "https://github.com/login/oauth/authorize";
               options.TokenEndpoint = "https://github.com/login/oauth/access_token";
               options.UserInformationEndpoint = "https://api.github.com/user";
               options.SaveTokens = true;
               options.Scope.Add("user:email");
               
               options.Events = new OAuthEvents
               {
                  OnCreatingTicket = async context =>
                  {
                     var request = new HttpRequestMessage(HttpMethod.Get, context.Options.UserInformationEndpoint);
                     request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", context.AccessToken);
                     request.Headers.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                     var response = await context.Backchannel.SendAsync(request,
                        HttpCompletionOption.ResponseHeadersRead, context.HttpContext.RequestAborted);
                     response.EnsureSuccessStatusCode();

                     var json = JsonDocument.Parse(await response.Content.ReadAsStringAsync());

                     if (json.RootElement.TryGetProperty("login", out var login))
                     {
                        context!.Identity!.AddClaim(new Claim("name", login.GetString()!));
                     }

                     string? emailValue = null;
                     if (json.RootElement.TryGetProperty("email", out var email))
                     {
                        emailValue = email.GetString();
                     }
                     
                     if (string.IsNullOrEmpty(emailValue))
                     {
                        var emailRequest = new HttpRequestMessage(HttpMethod.Get, "https://api.github.com/user/emails");
                        emailRequest.Headers.Authorization = new AuthenticationHeaderValue("Bearer", context.AccessToken);
                        emailRequest.Headers.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                        var emailResponse = await context.Backchannel.SendAsync(emailRequest,
                           HttpCompletionOption.ResponseHeadersRead, context.HttpContext.RequestAborted);
                        emailResponse.EnsureSuccessStatusCode();

                        var emailJson = JsonDocument.Parse(await emailResponse.Content.ReadAsStringAsync());
                        foreach (var emailElement in emailJson.RootElement.EnumerateArray())
                        {
                           if (emailElement.GetProperty("primary").GetBoolean())
                           {
                              emailValue = emailElement.GetProperty("email").GetString();
                              break;
                           }
                        }
                     }
                     
                     if (!string.IsNullOrEmpty(emailValue))
                     {
                        context!.Identity!.AddClaim(new Claim("email", emailValue));
                     }

                     if (json.RootElement.TryGetProperty("avatar_url", out var avatarUrl))
                     {
                        context!.Identity!.AddClaim(new Claim("avatar_url", avatarUrl.GetString()!));
                     }
                  },

                  OnRedirectToAuthorizationEndpoint = context =>
                  {
                     context.Response.Redirect(context.RedirectUri);
                     return Task.CompletedTask;
                  },

                  OnRemoteFailure = context =>
                  {
                     if (context.Failure != null && context.Failure.Message.Contains("state"))
                     {
                        return Task.FromResult(
                           ErrorResponseHelper.CreateErrorResponse(
                              500,
                              "OAuth error",
                              "oauth state is invalid or empty")
                        );
                     }

                     return Task.CompletedTask;
                  }
               };
            })
            .AddOAuth("google", options =>
            {
               options.SignInScheme = CookieAuthenticationDefaults.AuthenticationScheme;
               options.ClientId = configuration["GoogleOAuth:ClientId"]!;
               options.ClientSecret = configuration["GoogleOAuth:ClientSecret"]!;
               options.CallbackPath = "/api/auth/signin-google/";
               options.AuthorizationEndpoint =
                  "https://accounts.google.com/o/oauth2/auth";
               options.TokenEndpoint = "https://oauth2.googleapis.com/token";
               options.UserInformationEndpoint =
                  "https://www.googleapis.com/oauth2/v3/userinfo";
               options.SaveTokens = true;
               options.Scope.Add("profile");
               options.Scope.Add("email");

               options.Events = new OAuthEvents
               {
                  OnCreatingTicket = async context =>
                  {
                     var request = new HttpRequestMessage(HttpMethod.Get, context.Options.UserInformationEndpoint);
                     request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", context.AccessToken);
                     request.Headers.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                     var response = await context.Backchannel.SendAsync(request,
                        HttpCompletionOption.ResponseHeadersRead, context.HttpContext.RequestAborted);
                     response.EnsureSuccessStatusCode();

                     var json = JsonDocument.Parse(await response.Content.ReadAsStringAsync());
                     
                     if (json.RootElement.TryGetProperty("name", out var name))
                     {
                        context.Identity!.AddClaim(new Claim("name", name.GetString()!));
                     }

                     if (json.RootElement.TryGetProperty("email", out var email))
                     {
                        context.Identity!.AddClaim(new Claim("email", email.GetString()!));
                     }

                     if (json.RootElement.TryGetProperty("picture", out var avatarUrl)) ;
                     {
                        context.Identity!.AddClaim(new Claim("avatar_url", avatarUrl.GetString()!));
                     }
                  },

                  OnRedirectToAuthorizationEndpoint = context =>
                  {
                     context.Response.Redirect(context.RedirectUri);
                     return Task.CompletedTask;
                  },

                  OnRemoteFailure = context =>
                  {
                     Console.WriteLine("OAuth error: " + context.Failure?.Message);
                     return Task.CompletedTask;
                  }
               };
            });

         services.AddAuthorization();
      }
   }
}