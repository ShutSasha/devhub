//using AuthService.Extensions;

using AuthService.Extensions;
using AuthService.Helpers.Jwt;
using AuthService.Helpers.Security;
using AuthService.Helpers.ThirdParty;
using AuthService.Models;
using AuthService.Services;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Authentication.OAuth;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

var builder = WebApplication.CreateBuilder(args);
var configuration = builder.Configuration;
var services = builder.Services;

services.AddCors(options =>
{
    options.AddPolicy("AllowAPIGateway", policy =>
    {
        policy.WithHeaders().AllowAnyHeader();
        policy.WithHeaders().AllowCredentials();
        policy.WithOrigins("http://localhost:5295","http://localhost:3000","http://10.0.2.2:3000") 
            .AllowAnyMethod()
            .AllowAnyHeader();
    });
});


services.AddDistributedMemoryCache();

services.Configure<GoogleAuthOptions>(configuration.GetSection("GoogleOAuth"));

services.Configure<JwtOptions>(configuration.GetSection(nameof(JwtOptions)));
services.AddApiAuthentication(builder.Configuration);
services.AddAuthorization();

services.AddControllers();
services.AddEndpointsApiExplorer();
services.AddSwaggerGen();
services.AddHttpClient();

services.Configure<MongoDbSettings>(configuration.GetSection("MongoDbSettings"));
services.Configure<SenderDataSettings>(configuration.GetSection("SenderData"));
services.AddSingleton<IMongoClient>(sp =>
{
    var settings = sp.GetRequiredService<IOptions<MongoDbSettings>>().Value;
    return new MongoClient(settings.ConnectionUri);
});

services.AddSingleton<IMongoDatabase>(sp =>
{
    var mongoClient = sp.GetRequiredService<IMongoClient>();
    var settings = sp.GetRequiredService<IOptions<MongoDbSettings>>().Value;
    return mongoClient.GetDatabase(settings.DatabaseName);
});

services.AddAutoMapper(AppDomain.CurrentDomain.GetAssemblies());
services.AddScoped<MailService>();
services.AddScoped<PasswordHasher>();
services.AddScoped<JwtProvider>();
services.AddScoped<TokenService>();
services.AddScoped<AuthService.Services.AuthService>();

var app = builder.Build();

app.UseCors("AllowAPIGateway");

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseStaticFiles();
app.UseRouting();

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();
