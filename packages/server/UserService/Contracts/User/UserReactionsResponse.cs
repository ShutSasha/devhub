namespace UserService.Contracts.Posts;

public class UserReactionsResponse
{
   public List<string> LikedPosts { get; set; }
   public List<string> DislikedPosts { get; set; }
}