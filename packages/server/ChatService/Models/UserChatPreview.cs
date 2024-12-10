namespace ChatService.Models;

public class UserChatPreview
{
   public string ChatId { get; set; }
   public string LastMessage { get; set; }
   public DateTime Timestamp { get; set; }
   public ParticipantDetail Participants { get; set; } = new();
}