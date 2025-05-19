namespace TaskManager.Api.Dtos;

public record CreateTaskDTO(
    string Title,
    string? Description,
    string Status,
    string? Priority,
    DateOnly? DueDate,
    Guid? AssigneeId
);
