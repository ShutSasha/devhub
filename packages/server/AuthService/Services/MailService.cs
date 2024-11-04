using System.Net.Mail;
using AuthService.Models;
using Microsoft.Extensions.Options;
using MimeKit;
using SmtpClient = MailKit.Net.Smtp.SmtpClient;

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
      var email = new MimeMessage();
      email.From.Add(new MailboxAddress("DevHub", _senderDataSettings.SenderEmail));
      email.To.Add(new MailboxAddress("", recieverEmail));
      email.Subject = "Verification Code";

      var templatePath = Path.Combine(Directory.GetCurrentDirectory(), "Templates", "EmailTemplate.html");
      var htmlContent = await File.ReadAllTextAsync(templatePath);
      htmlContent = htmlContent.Replace("{{code}}", code);
      
      email.Body = new TextPart("html")
      {
         Text = htmlContent
      };
      
      using var smtp = new SmtpClient();
      try
      {
         await smtp.ConnectAsync("smtp.gmail.com", 587, MailKit.Security.SecureSocketOptions.StartTls);
         await smtp.AuthenticateAsync(_senderDataSettings.SenderEmail, _senderDataSettings.SenderPassword);
         await smtp.SendAsync(email);
      }
      finally
      {
         await smtp.DisconnectAsync(true);
      }
   }
   
}