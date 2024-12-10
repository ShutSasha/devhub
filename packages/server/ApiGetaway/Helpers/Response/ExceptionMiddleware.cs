namespace ApiGetaway.Helpers.Response;

public class ExceptionMiddleware
{
    private readonly RequestDelegate _next;
    private readonly ILogger<ExceptionMiddleware> _logger;

    public ExceptionMiddleware(RequestDelegate next, ILogger<ExceptionMiddleware> logger)
    {
        _next = next;
        _logger = logger;
    }

    public async Task Invoke(HttpContext context)
    {
        try
        {
            await _next(context);
            
            if (context.Response.StatusCode == StatusCodes.Status401Unauthorized && !context.Response.HasStarted)
            {
                _logger.LogInformation("Intercepting 401 response to return custom error message.");
                context.Response.Clear();
                
                var errorResponse = new
                {
                    status = 401,
                    errors = new Dictionary<string, string[]>
                    {
                        { "Auth error", new[] { "User is unauthorized" } }
                    }
                };
                

                context.Response.StatusCode = StatusCodes.Status401Unauthorized;
                context.Response.ContentType = "application/json";

                await context.Response.WriteAsJsonAsync(errorResponse);
            }
        }
        catch (UnauthorizedAccessException ex)
        {
            if (!context.Response.HasStarted)
            {
                _logger.LogWarning("Unauthorized access exception intercepted: {Message}", ex.Message);

                var errorResponse = new
                {
                    status = 401,
                    message = ex.Message
                };

                context.Response.Clear();
                context.Response.StatusCode = StatusCodes.Status401Unauthorized;
                context.Response.ContentType = "application/json";

                await context.Response.WriteAsJsonAsync(errorResponse);
            }
            else
            {
                _logger.LogWarning("Response has already started. Unable to set status for UnauthorizedAccessException.");
            }
        }
        catch (Exception ex)
        {
            if (!context.Response.HasStarted)
            {
                _logger.LogError(ex, "Unhandled exception: {Message}", ex.Message);

                var errorResponse = new
                {
                    status = 500,
                    message = "An unexpected error occurred."
                };

                context.Response.Clear();
                context.Response.StatusCode = StatusCodes.Status500InternalServerError;
                context.Response.ContentType = "application/json";

                await context.Response.WriteAsJsonAsync(errorResponse);
            }
            else
            {
                _logger.LogError("Response has already started. Unable to handle unhandled exception.");
            }
        }
    }
}
