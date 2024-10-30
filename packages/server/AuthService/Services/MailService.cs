using AuthService.Models;
using Microsoft.Extensions.Options;
using SendGrid;
using SendGrid.Helpers.Mail;

namespace AuthService.Services;

public class MailService
{
  private readonly SenderDataSettings _senderDataSettings;
   public MailService(IOptions<SenderDataSettings> senderDataSettings)
   {
     _senderDataSettings = senderDataSettings.Value;
   }
   
   public async Task SendVerificationCode(string recieverEmail, string code)
   {
      var apiKey = _senderDataSettings.SendGridKey;
      var client = new SendGridClient(apiKey);
      var from = new EmailAddress(_senderDataSettings.SenderEmail,"DevHub");
      var subject = "Activation link DevHub";
      var to = new EmailAddress(recieverEmail);
      var templatePath = Path.Combine(Directory.GetCurrentDirectory(), "Templates", "EmailTemplate.html");
      var htmlContent = await File.ReadAllTextAsync(templatePath);
      
      htmlContent = htmlContent.Replace("{{code}}", code);
      var plainTextContent = "";
      var msg = MailHelper.CreateSingleEmail(from, to, subject, plainTextContent, htmlContent);
      var response = await client.SendEmailAsync(msg);
   }
   
}