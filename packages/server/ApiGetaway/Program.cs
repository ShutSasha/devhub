using ApiGetaway.Helpers;
using ApiGetaway.Helpers.Response;
using Microsoft.OpenApi.Models;
using Ocelot.DependencyInjection;
using Ocelot.Middleware;

var builder = WebApplication.CreateBuilder(args);
var services = builder.Services;
var configuration = builder.Configuration;

services.AddCors(options =>
{
   options.AddPolicy("AllowMultipleOrigins", policy =>
   {
      policy.WithHeaders().AllowAnyHeader();
      policy.WithHeaders().AllowCredentials();
      policy.WithOrigins("http://localhost:3000", "http://10.0.2.2:3000")
         .AllowAnyHeader()
         .AllowAnyMethod();
   });
});

builder.Configuration.SetBasePath(builder.Environment.ContentRootPath)
   .AddJsonFile("ocelot.json", optional: false, reloadOnChange: true)
   .AddEnvironmentVariables();

services.AddCustomAuthentication(configuration);
services.AddOcelot(builder.Configuration);
services.AddAuthorization();
services.AddEndpointsApiExplorer();
services.AddSwaggerGen(c =>
{
   c.SwaggerDoc("v1", new OpenApiInfo { Title = "API Gateway", Version = "v1" });
});

var app = builder.Build();

//app.UseMiddleware<ExceptionMiddleware>();
app.UseCors("AllowMultipleOrigins");

if (app.Environment.IsDevelopment())
{
   app.UseSwagger();
   app.UseSwaggerUI(c =>
   {
      c.SwaggerEndpoint("http://localhost:5279/swagger/v1/swagger.json", "AuthService");
      c.SwaggerEndpoint("http://localhost:8080/swagger/doc.json", "PostService");
      c.SwaggerEndpoint("http://localhost:8081/swagger/doc.json", "CommentService");
      c.SwaggerEndpoint("http://localhost:5229/swagger/v1/swagger.json", "UserService");
      
      c.RoutePrefix = "";
   });
}
app.UseAuthentication();
app.UseAuthorization();
await app.UseOcelot();
app.Run();