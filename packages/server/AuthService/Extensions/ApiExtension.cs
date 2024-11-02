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
               //options.DefaultAuthenticateScheme = CookieAuthenticationDefaults.AuthenticationScheme;
               options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
               options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
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
                     }

                     return Task.CompletedTask;
                  },

                  OnChallenge = context =>
                  {
                     context.HandleResponse();

                     context.Response.StatusCode = StatusCodes.Status401Unauthorized;
                     context.Response.ContentType = "application/json";
                     var response = new
                     {
                        status = 401,
                        errors = new Dictionary<string, List<string>>
                        {
                           { "Authorization error", new List<string> { "User is unauthorized" } }
                        }
                     };

                     return context.Response.WriteAsync(JsonConvert.SerializeObject(response));
                  }
               };
            })
            .AddOAuth("github", o =>
            {
               o.SignInScheme = CookieAuthenticationDefaults.AuthenticationScheme;
               o.ClientId = "Ov23liFczWx5BDfrYPAf"; // или используйте configuration["Authentication:GitHub:ClientId"];
               o.ClientSecret =
                  "e209e0b3013cb95810a3e5d636adeb49ad8aeca0"; // или используйте configuration["Authentication:GitHub:ClientSecret"];

               o.AuthorizationEndpoint = "https://github.com/login/oauth/authorize";
               o.TokenEndpoint = "https://github.com/login/oauth/access_token";
               o.UserInformationEndpoint = "https://api.github.com/user";
               o.CallbackPath = new PathString("/api/auth/signin-github");

               o.ClaimActions.MapJsonKey("sub", "id");
               o.ClaimActions.MapJsonKey(ClaimTypes.Name, "login");

               o.Events.OnCreatingTicket = async ctx =>
               {
                  using var request = new HttpRequestMessage(HttpMethod.Get, ctx.Options.UserInformationEndpoint);
                  request.Headers.Authorization = new AuthenticationHeaderValue("Bearer", ctx.AccessToken);
                  request.Headers.Accept.Add(new MediaTypeWithQualityHeaderValue("application/json"));

                  using var result = await ctx.Backchannel.SendAsync(request);

                  if (result.IsSuccessStatusCode)
                  {
                     var user = await result.Content.ReadFromJsonAsync<JsonElement>();

                     // Здесь добавляем утверждения на основе данных о пользователе
                     ctx.Identity.AddClaim(new Claim(ClaimTypes.NameIdentifier, user.GetProperty("id").GetString()));
                     ctx.Identity.AddClaim(new Claim(ClaimTypes.Name, user.GetProperty("login").GetString()));
                     ctx.Identity.AddClaim(new Claim("avatar_url", user.GetProperty("avatar_url").GetString()));
                     // Добавьте дополнительные утверждения по необходимости
                  }
                  else
                  {
                     // Обработка ошибки, если запрос не был успешным
                     ctx.Fail("Failed to retrieve user information.");
                  }
               };
            })
            .AddCookie(CookieAuthenticationDefaults.AuthenticationScheme);

         services.AddAuthorization();
      }
   }
}