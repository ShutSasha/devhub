using Amazon.Runtime.Internal;
using Microsoft.AspNetCore.DataProtection.Internal;
using Microsoft.AspNetCore.Mvc;
using Swashbuckle.AspNetCore.Annotations;
using UserService.Abstracts;
using UserService.Contracts.Report;
using UserService.Helpers.Errors;
using UserService.Helpers.Response;

namespace UserService.Controllers;

[ApiController]
[Route("api/reports")]
public class ReportController : ControllerBase
{
   private readonly IReportService _reportService;

   public ReportController(IReportService reportService)
   {
      _reportService = reportService;
   }

   [HttpPost]
   [SwaggerOperation("Add report")]
   public async Task<IActionResult> AddReport([FromBody] CreateReportRequest request)
   {
      try
      {
         var reportCreationResult = await _reportService
            .AddReport(request.Sender, request.Content, request.Category);
         
         return Ok(reportCreationResult);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(e.Message.Split(":")[0]),
            nameof(AddReport),
            e.Message.Split(":")[1]);
      }
   }

   [HttpGet("{userId}")]
   [SwaggerOperation("Get user reports")]
   public async Task<IActionResult> GetByUserId([FromRoute] [ObjectIdValidation] string userId)
   {
      try
      {
         var reports = await _reportService.GetUserReports(userId);

         return Ok(reports);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(e.Message.Split(":")[0]),
            nameof(GetByUserId),
            e.Message.Split(":")[1]);
      }
   }

   [HttpDelete("{reportId}")]
   [SwaggerOperation("Delete report")]
   public async Task<IActionResult> DeletePost([FromRoute] [ObjectIdValidation] string reportId)
   {
      try
      {
         var reportDeletionResult = await _reportService.DeleteReport(reportId);

         return Ok(reportDeletionResult);
      }
      catch (Exception e)
      {
         return ErrorResponseHelper.CreateErrorResponse(
            Convert.ToInt32(e.Message.Split(":")[0]),
            nameof(DeletePost),
            e.Message.Split(":")[1]);
      }
   }
   
}