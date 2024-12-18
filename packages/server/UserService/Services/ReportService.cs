using MongoDB.Driver;
using UserService.Abstracts;
using UserService.Models.Report;

namespace UserService.Services;

public class ReportService : IReportService
{
   private readonly IUserService _userService;
   private readonly IMongoCollection<Report> _reportCollection;
   private readonly IMongoCollection<Models.User.Post> _postCollection;
   

   public ReportService(IMongoDatabase mongoDatabase, IUserService userService)
   {
      _userService = userService;
      _reportCollection = mongoDatabase.GetCollection<Report>("reports");
      _postCollection = mongoDatabase.GetCollection<Models.User.Post>("posts");
   }

   public async Task<List<Report>> GetUserReports(string userId)
   {
      var candidate = await _userService.GetById(userId);

      if (candidate == null)
      {
         throw new Exception("404: User wasn't found");
      }
      
      var userReports = await _reportCollection
         .Find(r => r.Sender == userId)
         .ToListAsync();

      if (userReports.Count == 0)
      {
         return new List<Report>();
      }

      return userReports;
   }

   public async Task<Report> AddReport(string sender, string content, string category)
   {
      var candidate = await _userService.GetById(sender);

      if (candidate == null)
      {
         throw new Exception("404: User wasn't found");
      }

      var report = new Report()
      {
         Category = category,
         Sender = sender,
         Content = content,
      };

      await _reportCollection.InsertOneAsync(report);

      var update = Builders<Models.User.Post>.Update.AddToSet(p => p.Reports, report.Id);
      
      var updateResult = await _postCollection.UpdateOneAsync(p => p.Id == content, update);
      
      if (updateResult.ModifiedCount == 0)
      {
         throw new Exception("500: Failed to update the post with the new report.");
      }

      return report;
   }

   public async Task<string> DeleteReport(string reportId)
   {
      var report = await _reportCollection.Find(r => r.Id == reportId).FirstOrDefaultAsync();

      if (report == null)
      {
         throw new Exception("404: Report not found");
      }

      var deleteResult = await _reportCollection.DeleteOneAsync(r => r.Id == reportId);

      if (deleteResult.DeletedCount == 0)
      {
         throw new Exception("500: Failed to delete the report");
      }

      var postFilter = Builders<Models.User.Post>.Filter.AnyEq(p => p.Reports, reportId);
      var update = Builders<Models.User.Post>.Update.Pull(p => p.Reports, reportId);

      await _postCollection.UpdateOneAsync(postFilter, update);

      return reportId;
   }

   
}