using System.Net;
using Amazon.S3;
using Amazon.S3.Model;
using Microsoft.Extensions.Options;
using UserService.Abstracts;
using UserService.Models.Storage;

namespace UserService.Services;

public class StorageService : IStorageService
{
   private readonly IAmazonS3 _s3Client;
   private readonly AwsOptions _options;
   private readonly ILogger<StorageService> _logger;

   public StorageService(IAmazonS3 s3Client, IOptions<AwsOptions> options, ILogger<StorageService> logger)
   {
      _s3Client = s3Client;
      _logger = logger;
      _options = options.Value;
   }

   public async Task<string> UploadFileAsync(string id, string key, Stream fileStream, string contentType)
   {
      var request = new PutObjectRequest
      {
         BucketName = _options.BucketName,
         Key = "user_icons/" + $"{id}/" + DateTime.Now.ToString("HH:mm:ss") + key,
         InputStream = fileStream,
         ContentType = contentType
      };

      try
      {
         var response = await _s3Client.PutObjectAsync(request);

         if (response.HttpStatusCode == HttpStatusCode.OK)
         {
            return $"https://mydevhubimagebucket.s3.eu-west-3.amazonaws.com/{request.Key}";
         }

         throw new Exception("File upload failed");
      }
      catch (AmazonS3Exception e)
      {
         throw new AmazonS3Exception($"500: {e.Message}");
      }
      catch (Exception e)
      {
         throw new Exception($"500:{e.Message}");
      }
   }

   public async Task DeleteFileAsync(string avatarPath)
   {
      var uri = new Uri(avatarPath);
      var key = uri.AbsolutePath.TrimStart('/');

      var request = new DeleteObjectRequest
      {
         BucketName = _options.BucketName,
         Key = key,
      };

      try
      {
         var response = await _s3Client.DeleteObjectAsync(request);

         if (response.HttpStatusCode == HttpStatusCode.NoContent)
         {
            _logger.LogInformation($"User icon with {avatarPath} path successfully deleted");
            return;
         }
         else
         {
            throw new Exception($"{response.HttpStatusCode}: Failed to delete file {avatarPath}.");
         }
      }
      catch (AmazonS3Exception e)
      {
         throw new AmazonS3Exception($"500: {e.Message}");
      }
      catch (Exception e)
      {
         throw new Exception($"500:{e.Message}");
      }
   }
}