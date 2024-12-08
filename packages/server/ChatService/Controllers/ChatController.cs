using ChatService.Models;
using ChatService.Models.Database;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Options;
using MongoDB.Driver;
using Swashbuckle.AspNetCore.Annotations;

namespace ChatService.Controllers;

[ApiController]
[Route("api/chat")]
public class ChatController : ControllerBase
{
   private readonly IMongoCollection<Chat> _userCollection;
   
   public ChatController()
   {
      
   }
   
   [HttpPost]
   [SwaggerOperation("Add chat")]
   public async Task<IActionResult> CreateChat()
   {
      return Ok();
   }

}