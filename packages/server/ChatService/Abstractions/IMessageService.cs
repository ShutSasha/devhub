using ChatService.Models;

namespace ChatService.Abstractions;

public interface IMessageService
{
   Task<Message> GetById(string messageId);
   Task<List<Message>> GetMessagesByChatId(string chatId);
}