using System.Net;
using Amazon.S3;
using Microsoft.AspNetCore.Server.Kestrel.Core;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using Post;
using UserService.Abstracts;
using UserService.Helpers.Config;
using UserService.Models.Database;
using UserService.Models.Storage;
using UserService.Services;

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

services.AddGrpcClient<PostService.PostServiceClient>(option =>
{
   option.Address = new Uri("http://localhost:5226");
});

services.AddControllers();
services.AddEndpointsApiExplorer();
services.AddSwaggerGen();
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

services.AddAutoMapper(AppDomain.CurrentDomain.GetAssemblies());
services.AddGrpc();
services.Configure<AwsOptions>(configuration.GetSection("AWS"));
services.AddSingleton<IAmazonS3>(AwsS3ClientFactory.CreateS3Client(configuration));
services.AddScoped<IUserService,UserService.Services.UserService>();
services.AddScoped<IStorageService, StorageService>();


var app = builder.Build();
app.UseCors("AllowLocalPorts");

if (app.Environment.IsDevelopment())
{
   app.UseSwagger();
   app.UseSwaggerUI();
}

app.UseRouting();
app.UseGrpcWeb(new GrpcWebOptions { DefaultEnabled = true });

app.MapGrpcService<UserGrpcService>().EnableGrpcWeb();
app.MapControllers();

app.Run();