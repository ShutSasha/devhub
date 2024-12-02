using UserService.Helpers.Errors;

namespace UserService.Contracts.User;

public record UserFollowingsRequest
{
   [ObjectIdValidation] 
   public string userId { get; set; }
   
   [ObjectIdValidation] 
   public string followingUserId { get; set; }
}