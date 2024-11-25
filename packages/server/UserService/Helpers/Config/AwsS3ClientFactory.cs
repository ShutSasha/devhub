using Amazon;
using Amazon.Runtime;
using Amazon.S3;
using UserService.Models.Storage;

namespace UserService.Helpers.Config;

public static class AwsS3ClientFactory
{
   public static IAmazonS3 CreateS3Client(IConfiguration configuration)
   {
      var awsOptions = configuration.GetSection("AWS").Get<AwsOptions>();
      var credentials = new BasicAWSCredentials(awsOptions?.AccessKey, awsOptions?.SecretKey);
      var config = new AmazonS3Config
      {
         RegionEndpoint = RegionEndpoint.EUWest3
      };

      return new AmazonS3Client(credentials, config);
      
   }
}