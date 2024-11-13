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

   public StorageService(IAmazonS3 s3Client, IOptions<AwsOptions> options)
   {
      _s3Client = s3Client;
      _options = options.Value;
   }

   public async Task<string> UploadFileAsync(string id, string key, Stream fileStream, string contentType)
   {
      var request = new PutObjectRequest
      {
         BucketName = _options.BucketName,
         Key = "user_icons/" + $"{id}/" + DateTime.Now.ToString("HH:mm:ss") + key,
         InputStream = fileStream,
         ContentType = contentType,
         //CannedACL = S3CannedACL.PublicRead
      };

      try
      {
         var response = await _s3Client.PutObjectAsync(request);

         if (response.HttpStatusCode == HttpStatusCode.OK)
         {
            return $"https://{_options.BucketName}.s3.amazonaws.com/{key}";
         }
         throw new Exception("File upload failed");
      }
      catch (AmazonS3Exception e)
      {
         // Логирование ошибок S3
         Console.WriteLine($"AmazonS3Exception: {e.Message}");
         throw;
      }
      catch (Exception e)
      {
         Console.WriteLine($"Exception: {e.Message}");
         throw;
      }
   }
}