using System.Text;
using AuthService.Helpers.Jwt;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;

namespace AuthService.Extensions;

public static class ApiExtension
{
   public static void AddApiAuthentication(this IServiceCollection services,
      IConfiguration configuration)
   {
      var jwtOptions = new JwtOptions();
      configuration.GetSection(nameof(JwtOptions)).Bind(jwtOptions);

      services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
         .AddJwtBearer(JwtBearerDefaults.AuthenticationScheme, options =>
         {
            options.TokenValidationParameters = new TokenValidationParameters
            {
               ValidateIssuer = false,
               ValidateAudience = false,
               ValidateLifetime = true,
               ValidateIssuerSigningKey = true,
               ClockSkew = TimeSpan.Zero,
               IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtOptions.SecretKey))
            };

            options.Events = new JwtBearerEvents
            {
               OnMessageReceived = context =>
               {
                  context.Token = context.Request.Cookies["tasty-cookies"];

                  return Task.CompletedTask;
               }
            };
         });

      services.AddAuthorization();
   }
}