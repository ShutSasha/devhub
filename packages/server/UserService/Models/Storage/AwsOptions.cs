namespace UserService.Models.Storage;

public class AwsOptions
{
   public string BucketName { get; set; } = null!;
   public string Region { get; set; } = null!;
   public string AccessKey { get; set; } = null!;
   public string SecretKey { get; set; } = null!;
}