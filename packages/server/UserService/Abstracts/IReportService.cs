using UserService.Models.Report;

namespace UserService.Abstracts;

public interface IReportService
{
   Task<List<Report>> GetUserReports(string userId);
   Task<Report> AddReport(string sender, string content, string type);
   Task<string> DeleteReport(string reportId);
}