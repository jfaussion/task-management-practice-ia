namespace TaskManager.Api.Dtos;

public record ErrorResponse(
    Guid ErrorId,
    DateTime TimeStamp,
    int Status,
    string Error,
    string Message,
    string Path
);
