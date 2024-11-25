using System.ComponentModel.DataAnnotations;

namespace UserService.Helpers.Errors;

public class ObjectIdValidationAttribute : ValidationAttribute
{
   public override bool IsValid(object value)
   {
      if (value == null || !(value is string))
      {
         return false;
      }

      var stringValue = (string)value;
      
      return stringValue.Length == 24 && System.Text.RegularExpressions.Regex.IsMatch(stringValue, @"^[a-fA-F0-9]{24}$");
   }

   public override string FormatErrorMessage(string name)
   {
      return $"{name} must be a valid ObjectId (24 hex characters).";
   }
}