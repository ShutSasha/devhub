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

builder.Services.AddCors(options =>
{
    //TODO: Change Cors policies
    options.AddPolicy("AllowAll",  
        builder => builder.AllowAnyOrigin().AllowAnyMethod().AllowAnyHeader());  
}); 


builder.Services.AddDistributedMemoryCache();

builder.Services.Configure<GoogleAuthOptions>(configuration.GetSection("GoogleAuthOptions"));

builder.Services.Configure<JwtOptions>(configuration.GetSection(nameof(JwtOptions)));
builder.Services.AddApiAuthentication(builder.Configuration);
builder.Services.AddAuthorization();

builder.Services.AddControllers();
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();
builder.Services.AddHttpClient();

builder.Services.Configure<MongoDbSettings>(configuration.GetSection("MongoDbSettings"));
builder.Services.Configure<SenderDataSettings>(configuration.GetSection("SenderData"));
builder.Services.AddSingleton<IMongoClient>(sp =>
{
    var settings = sp.GetRequiredService<IOptions<MongoDbSettings>>().Value;
    return new MongoClient(settings.ConnectionUri);
});

builder.Services.AddSingleton<IMongoDatabase>(sp =>
{
    var mongoClient = sp.GetRequiredService<IMongoClient>();
    var settings = sp.GetRequiredService<IOptions<MongoDbSettings>>().Value;
    return mongoClient.GetDatabase(settings.DatabaseName);
});

builder.Services.AddAutoMapper(AppDomain.CurrentDomain.GetAssemblies());
builder.Services.AddScoped<MailService>();
builder.Services.AddScoped<PasswordHasher>();
builder.Services.AddScoped<JwtProvider>();
builder.Services.AddScoped<TokenService>();
builder.Services.AddScoped<AuthService.Services.AuthService>();

var app = builder.Build();

app.UseCors("AllowAll");

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
