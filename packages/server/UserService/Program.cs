using Microsoft.Extensions.Options;
using MongoDB.Driver;
using UserService.Models.Database;
using UserService.Services;

var builder = WebApplication.CreateBuilder(args);
var configuration = builder.Configuration;
builder.Services.AddCors(options =>
{
   options.AddPolicy("AllowLocalPorts", policy =>
   {
      policy.WithHeaders().AllowCredentials();
      policy.WithHeaders().AllowAnyHeader();
      policy.WithOrigins("http://localhost:3000", "http://localhost:5295");
   });
});

builder.Services.Configure<MongoDbSettings>(configuration.GetSection("MongoDbSettings"));
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


builder.Services.AddGrpc();

var app = builder.Build();

app.UseCors("AllowLocalPorts");
app.MapGrpcService<UserService.Services.UserService>();

app.Run();
