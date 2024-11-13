namespace UserService.Abstracts;

public interface IStorageService
{
   Task<string> UploadFileAsync(string id, string key, Stream fileStream, string contentType);
}