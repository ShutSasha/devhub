using ChatService.Models;

namespace ChatService.Contracts;

public class ChatResponse
{
   public string ChatId { get; set; }
   public ParticipantDetail ParticipantDetails { get; set; }
   public List<Message> ChatMessages { get; set; }
}