using Microsoft.AspNetCore.Diagnostics;
using TaskManager.Api.Dtos;
using TaskManager.Domain.Exceptions;

namespace TaskManager.Api.Exceptions;

internal sealed class GlobalExceptionHandler(ILogger<GlobalExceptionHandler> logger) : IExceptionHandler
{
    private readonly ILogger<GlobalExceptionHandler> _logger = logger;

    public async ValueTask<bool> TryHandleAsync(HttpContext httpContext, Exception exception, CancellationToken cancellationToken)
    {
        var errorId = Guid.NewGuid();
        _logger.LogError(
            exception, $"Exception occurred [{errorId}]: {exception.Message}");

        string requestPath = httpContext.Request.Path.Value ?? string.Empty;
        ErrorResponse errorResponse;
        switch (exception)
        {
            case ArgumentException:
                errorResponse = CreateArgumentErrorResponse(errorId, exception.Message, requestPath);
                break;
            case FunctionalException:
                errorResponse = CreateFunctionalErrorResponse(errorId, exception.Message, requestPath);
                break;
            case TechnicalException:
                errorResponse = CreateTechnicalErrorResponse(errorId, requestPath);
                break;
            default:
                errorResponse = CreateInternalErrorResponse(errorId, requestPath);
                break;
        }

        httpContext.Response.StatusCode = errorResponse.Status;

        await httpContext.Response
            .WriteAsJsonAsync(errorResponse, cancellationToken);

        return true;
    }

    private static ErrorResponse CreateFunctionalErrorResponse(Guid errorId, string message, string path)
    {
        return new ErrorResponse(
            ErrorId: errorId,
            TimeStamp: DateTime.UtcNow,
            Status: StatusCodes.Status400BadRequest,
            Error: "Functional Error",
            Message: message,
            Path: path);
    }

    private static ErrorResponse CreateArgumentErrorResponse(Guid errorId, string message, string path)
    {
        return new ErrorResponse(
            ErrorId: errorId,
            TimeStamp: DateTime.UtcNow,
            Status: StatusCodes.Status400BadRequest,
            Error: "Validation Error",
            Message: message,
            Path: path);
    }

    private static ErrorResponse CreateTechnicalErrorResponse(Guid errorId, string path)
    {
        return new ErrorResponse(
            ErrorId: errorId,
            TimeStamp: DateTime.UtcNow,
            Status: StatusCodes.Status500InternalServerError,
            Error: "Technical Error",
            Message: "An unexpected error occurred. Please try again later.",
            Path: path);
    }

    private static ErrorResponse CreateInternalErrorResponse(Guid errorId, string path)
    {
        return new ErrorResponse(
            ErrorId: errorId,
            TimeStamp: DateTime.UtcNow,
            Status: StatusCodes.Status500InternalServerError,
            Error: "Internal Server Error",
            Message: "An unexpected error occurred. Please try again later.",
            Path: path);
    }
}
