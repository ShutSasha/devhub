using ChatService.Abstractions;
using ChatService.Contracts;
using ChatService.Helpers.Response;
using ChatService.Models;
using ChatService.Models.Database;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using Swashbuckle.AspNetCore.Annotations;

namespace ChatService.Controllers;  
  
[ApiController]  
[Route("api/chat")]  
public class ChatController : ControllerBase  
{  
   private readonly IChatService _chatService;  
  
   public ChatController(IChatService chatService)  
   {  
      _chatService = chatService;  
   }  
  
   [HttpPost]  
   [SwaggerOperation("Add chat")]  
   public async Task<IActionResult> CreateChat([FromQuery] CreateChatRequest request)  
   {  
      try  
      {  
         var chatCreationResult = await _chatService.CreateChat(request.UserId, request.TargetUserId);  
  
         return Ok(new { ChatId = chatCreationResult });  
      }  
      catch (Exception e)  
      {  
         return ErrorResponseHelper.CreateErrorResponse(  
            Convert.ToInt32(e.Message.Split(":")[0]),  
            nameof(CreateChat),  
            e.Message);  
      }  
   }

   [HttpDelete]
   [SwaggerOperation("Delete chat")]
   public async Task<IActionResult> DeleteChat([FromQuery] string chatId)
   {
      try
      {
         await _chatService.DeleteChat(chatId);
         return Ok();
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(  
            Convert.ToInt32(e.Message.Split(":")[0]),  
            nameof(CreateChat),  
            e.Message); 
      }
   }

   [HttpGet("chat-details/{userId}")]
   [SwaggerOperation("Get chat details")]
   public async Task<IActionResult> GetChatDetails([FromRoute] string userId)
   {
      try
      {
         var chatDetails = await _chatService.GetUserChats(userId);
         return Ok(chatDetails);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(  
            Convert.ToInt32(e.Message.Split(":")[0]),  
            nameof(GetChatDetails),  
            e.Message); 
      }
      
   }
}