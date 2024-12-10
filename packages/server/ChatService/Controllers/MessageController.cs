using ChatService.Abstractions;
using ChatService.Helpers.Response;
using ChatService.Models;
using Microsoft.AspNetCore.Mvc;
using Swashbuckle.AspNetCore.Annotations;

namespace ChatService.Controllers;

[ApiController]
[Route("api/messages")]
public class MessageController : ControllerBase
{
   private readonly IMessageService _messageService;

   public MessageController(IMessageService messageService)
   {
      _messageService = messageService;
   }

   [HttpGet("{chatId}")]
   [SwaggerOperation("Get list of messages by chat id")]
   [ProducesResponseType(200, Type = typeof(List<Message>))]
   public async Task<IActionResult> GetMessagesByChatId([FromRoute] string chatId)
   {
      try
      {
         var messages = await _messageService.GetMessagesByChatId(chatId);
         return Ok(messages);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(  
            Convert.ToInt32(e.Message.Split(":")[0]),  
            nameof(GetMessagesByChatId),  
            e.Message);
      }
   }
}