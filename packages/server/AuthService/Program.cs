using AuthService.Extensions;
using AuthService.Helpers.Jwt;
using AuthService.Helpers.Security;
using AuthService.Models;
using AuthService.Services;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

var builder = WebApplication.CreateBuilder(args);
var configuration = builder.Configuration;

builder.Services.AddCors(options =>
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
builder.Services.AddScoped<PasswordHasher>();
builder.Services.AddScoped<JwtProvider>();
builder.Services.AddScoped<MailService>();
builder.Services.AddScoped<TokenService>();
builder.Services.AddScoped<AuthService.Services.AuthService>();

var app = builder.Build();

app.UseCors("AllowAPIGateway");

if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

app.UseHttpsRedirection();

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();
