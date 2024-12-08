using UserService.Helpers.Errors;

namespace UserService.Contracts.User;

public record UserFollowingsRequest
{
   [ObjectIdValidation] 
   public string UserId { get; set; }
   
   [ObjectIdValidation] 
   public string FollowingUserId { get; set; }
}