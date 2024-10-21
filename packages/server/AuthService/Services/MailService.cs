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
     var apiKey = _senderDataSettings.SendGripKey;
      var client = new SendGridClient(apiKey);
      var from = new EmailAddress(_senderDataSettings.SenderEmail);
      var subject = "Activation link DevHub";
      var to = new EmailAddress(recieverEmail);
      var plainTextContent = "";
      var htmlContent = $@"<html>
      <head>
        <style>
          .container {{
            text-align: center;
            padding: 50px 20px;
          }}
          .content {{
            background-color: #2A2A2D;
            padding: 40px;
            border-radius: 10px;
            display: inline-block;
            max-width: 600px;
            width: 100%;
          }}
          h1 {{
            font-size: 24px;
            color: #ffffff;
            margin-bottom: 20px;
          }}
          p {{
            font-size: 16px;
            color: #bbbbbb;
            margin-bottom: 30px;
          }}
          .message__code{{
            color: #bbbbbb;
            font-size: 24px;
            word-spacing: 5px;
          }}
        </style>
      </head>
      <body>
        <div class='container'>
          <div class='content'>
            <h1>Welcome to DevHub!</h1>
            <p>Please,copy this code and paste it in verification page</p>
            <span class='message__code'>{code}</span>
          </div>
        </div>
      </body>
    </html>";
      var msg = MailHelper.CreateSingleEmail(from, to, subject, plainTextContent, htmlContent);
      var response = await client.SendEmailAsync(msg);
   }
   
}