using ChatService;
using ChatService.Abstractions;
using ChatService.Hubs;
using ChatService.Models.Database;
using ChatService.Services;
using Microsoft.Extensions.Options;
using MongoDB.Driver;

var builder = WebApplication.CreateBuilder(args);
var configuration = builder.Configuration;
var services = builder.Services;

builder.Services.AddCors(options =>
{
   options.AddPolicy("AllowLocalPorts", policy =>
   {
      policy.WithHeaders().AllowCredentials();
      policy.WithHeaders().AllowAnyHeader();
      policy.WithOrigins("http://localhost:5295", "http://localhost:3000")
         .AllowAnyMethod()
         .AllowAnyHeader();
   });
});

services.AddControllers();
services.Configure<MongoDbSettings>(configuration.GetSection("MongoDbSettings"));
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
services.AddGrpcClient<UserChatService.UserChatServiceClient>(option =>
{
   option.Address = new Uri("http://localhost:5228");
});

services.AddSignalR();
services.AddEndpointsApiExplorer();
services.AddSwaggerGen(options => { options.EnableAnnotations(); });

services.AddScoped<IChatService, ChatService.Services.ChatService>();
services.AddScoped<IMessageService, MessageService>();

var app = builder.Build();

app.UseCors("AllowLocalPorts");

app.MapHub<ChatHub>("/chat");

if (app.Environment.IsDevelopment())
{
   app.UseSwagger();
   app.UseSwaggerUI();
}

app.UseHttpsRedirection();
app.MapControllers();

app.Run();