using Microsoft.AspNetCore.Mvc;
using Swashbuckle.AspNetCore.Annotations;
using UserService.Abstracts;
using UserService.Contracts.User;
using UserService.Dto;
using UserService.Helpers.Errors;
using UserService.Helpers.Response;
using Type = Google.Protobuf.WellKnownTypes.Type;

namespace UserService.Controllers;

[ApiController]
[Route("api/users")]
public class UserController : ControllerBase
{

   private readonly IUserService userService;

   public UserController(IUserService userService)
   {
      this.userService = userService;
   }
   
   [HttpPatch]
   [SwaggerOperation("Edit user model")]
   [ProducesResponseType(200,Type = typeof(UserDto))]
   public async Task<IActionResult> EditUser([FromBody] EditUserRequest request)
   {
      try
      {
         var userInformation = await userService.EditUser(request.Id, request.Name, request.Bio);
         return Ok(new { User = userInformation });
      }
      catch (Exception e)
      {
         int statusCode = Convert.ToInt32(e.Message.Split(":")[0]);
         return ErrorResponseHelper.CreateErrorResponse(
            statusCode,
            "Edit user error",
            e.Message.Split(":")[1]
         );
      }
   }

   [HttpPost("update-photo/{userId}")]
   [SwaggerOperation("Update user avatar")]
   public async Task<IActionResult> UpdateUserPhoto(IFormFile file, [FromRoute] [ObjectIdValidation] string userId)
   {
      if (file.Length == 0)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            400,
            "File upload Exception",
            "File is empty or wasn't send");
      }

      try
      {
         await using var fileStream = file.OpenReadStream();
         var fileName = file.FileName;
         var contentType = file.ContentType;
         var updateUserIconResult = await userService.EditUserIcon(userId, fileName, fileStream, contentType);

         return Ok(new { Avatar = updateUserIconResult });
      }
      catch (Exception ex)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(ex.Message.Split(":")[0]),
            "Update user error",
            ex.Message.Split(":")[1]);
      }
   }

   [HttpGet("user-details/{userId}")]
   [SwaggerOperation("Get user data")]
   [ProducesResponseType(200,Type =typeof(UserDetailsResponse))]
   public async Task<IActionResult> GetUserDetails([ObjectIdValidation] string userId)
   {
      try
      {
         var userDetails = await userService.GetUserDetailsById(userId);
         return Ok(userDetails);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(400, nameof(GetUserDetails), e.Message);
      }
   }

   [HttpGet("user-reactions/{userId}")]
   [SwaggerOperation("Get user liked and disliked posts")]
   [ProducesResponseType(200,Type =typeof(UserReactionsResponse))]
   public async Task<IActionResult> GetUserReactions([ObjectIdValidation] string userId)
   {
      try
      {
         var userReactions = await userService.GetUserReaction(userId);

         return Ok(userReactions);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(e.Message.Split(":")[0]),
            nameof(GetUserDetails),
            e.Message);
      }
   }

   [HttpPost("user-followings")]
   [SwaggerOperation("Add user following")]
   [ProducesResponseType(200, Type = typeof(UserDto))]
   public async Task<IActionResult> AddUserFollowing([FromBody] UserFollowingsRequest request)
   {
      try
      {
         var userUpdateResult = await userService.AddUserFollowing(request.UserId, request.FollowingUserId);
         return Ok(userUpdateResult);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(e.Message.Split(":")[0]),
            nameof(AddUserFollowing),
            e.Message);
      }
   }

   [HttpDelete("user-followings")]
   [SwaggerOperation("Delete user following")]
   [ProducesResponseType(200, Type = typeof(UserDto))]
   public async Task<IActionResult> RemoveUserFollowing([FromQuery] UserFollowingsRequest request)
   {
      try
      {
         var userUpdateResult = await userService.RemoveUserFollowing(request.UserId, request.FollowingUserId);
         return Ok(userUpdateResult);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(e.Message.Split(":")[0]),
            nameof(AddUserFollowing),
            e.Message);
      }
   }

   [HttpGet("user-followings/{userId}")]
   [SwaggerOperation("Get list of user followings")]
   [ProducesResponseType(200,Type =typeof(List<UserConnectionsDto>))]
   public async Task<IActionResult> GetUserFollowings([FromRoute] [ObjectIdValidation] string userId)
   {
      try
      {
         var userFollowings = await userService.GetUserConnections(userId, "followings");
         return Ok(userFollowings);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(e.Message.Split(":")[0]),
            nameof(AddUserFollowing),
            e.Message);
      }
   }

   [HttpGet("user-followers/{userId}")]
   [SwaggerOperation("Get list of user followers")]
   [ProducesResponseType(200,Type =typeof(List<UserConnectionsDto>))]
   public async Task<IActionResult> GetUserFollowers([FromRoute] string userId)
   {
      try
      {
         var userFollowers = await userService.GetUserConnections(userId, "followers");
         return Ok(userFollowers);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(e.Message.Split(":")[0]),
            nameof(AddUserFollowing),
            e.Message);
      }
   }

   [HttpGet("is-following")]
   [SwaggerOperation("Check whether the user is following")]
   [ProducesResponseType(200, Type = typeof(bool))]
   public async Task<IActionResult> IsFollowing([FromQuery] [ObjectIdValidation] string userId,
      [FromQuery] [ObjectIdValidation] string targetUserId)
   {
      try
      {
         var isUserFollowed = await userService.CheckUserFollowing(userId, targetUserId);
         return Ok(isUserFollowed);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(e.Message.Split(":")[0]),
            nameof(AddUserFollowing),
            e.Message);
      }
   }

   [HttpPost("saved-posts")]
   [SwaggerOperation("Update saved post")]
   public async Task<IActionResult> AddSavedPost([FromBody] UserSavedPostRequest request)
   {
      try
      {
         var updateSavedPostResult  = await userService.UpdateSavedPost(request.UserId, request.SavedPostId);
         return Ok(updateSavedPostResult);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(e.Message.Split(":")[0]),
            nameof(AddUserFollowing),
            e.Message);
      }
   }

   [HttpGet("saved-posts/{userId}")]
   [SwaggerOperation("Get saved list of saved posts id")]
   [ProducesResponseType(200,Type = typeof(List<string>))]
   public async Task<IActionResult> GetUserSavedPosts([FromRoute] string userId)
   {
      try
      {
         var savedPosts = await userService.GetSavedPosts(userId);
         return Ok(new { SavedPosts = savedPosts });
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(e.Message.Split(":")[0]),
            nameof(AddUserFollowing),
            e.Message);
      }
   }

   [HttpGet("saved-posts-details/{userId}")]
   [SwaggerOperation("Get list of saved posts details by user id")]
   [ProducesResponseType(200, Type = typeof(List<PostDto>))]
   public async Task<IActionResult> GetUserSavedPostsDetails([FromRoute] string userId)
   {
      try
      {
         var detailedSavedPosts = await userService.GetDetailedSavedPosts(userId);
         return Ok(detailedSavedPosts);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(e.Message.Split(":")[0]),
            nameof(AddUserFollowing),
            e.Message);
      }      
   }
}