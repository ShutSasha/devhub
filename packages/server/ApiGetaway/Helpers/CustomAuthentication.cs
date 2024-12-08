using System.Text;
using ApiGetaway.Helpers.Jwt;
using ApiGetaway.Helpers.Response;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.IdentityModel.Tokens;
using Newtonsoft.Json;

namespace ApiGetaway.Helpers;

public static class CustomAuthentication
{
   public static void AddCustomAuthentication(this IServiceCollection services,IConfiguration configuration)
   {
      var jwtOptions = new JwtOptions();
      configuration.GetSection(nameof(JwtOptions)).Bind(jwtOptions);
      services.Configure<JwtOptions>(configuration.GetSection(nameof(JwtOptions)));

      services.AddAuthentication(options =>
      {
         options.DefaultAuthenticateScheme = JwtBearerDefaults.AuthenticationScheme;
         options.DefaultChallengeScheme = JwtBearerDefaults.AuthenticationScheme;
      }).AddJwtBearer("Bearer", options =>
      {
         
         options.Events = new JwtBearerEvents
         {
            OnChallenge = async context =>
            {

               context.HandleResponse();

               var response = new
               {
                  StatusCode = 401,
                  Error = "Unauthorized",
                  Message = "Access token is missing or invalid."
               };

               context.Response.StatusCode = StatusCodes.Status401Unauthorized;
               context.Response.ContentType = "application/json";

               await context.Response.WriteAsync(JsonConvert.SerializeObject(response));
            },
            OnAuthenticationFailed = context =>
            {
               context.Response.StatusCode = StatusCodes.Status401Unauthorized;
               context.Response.ContentType = "application/json";

               var response = ErrorResponseHelper.CreateErrorResponse(
                  401,
                  "Auth error",
                  "Token validation failed");

               return context.Response.WriteAsync(JsonConvert.SerializeObject(response));
            }
         };
         
         //TODO: Change to environment parameters
         options.RequireHttpsMetadata = false;
         options.SaveToken = true;

         options.TokenValidationParameters = new TokenValidationParameters
         {
            ValidateIssuer = false,
            ValidateAudience = false,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ClockSkew = TimeSpan.Zero,
            IssuerSigningKey = new SymmetricSecurityKey(Encoding.UTF8.GetBytes(jwtOptions.AccessSecretKey))
         };
      });
   }
}