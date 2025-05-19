namespace TaskManager.Api.Dtos;

public record TaskDTO(
    Guid Id,
    string Title,
    string? Description,
    string Status,
    string? Priority,
    Guid? AssigneeId,
    UserDTO? Assignee,
    DateOnly? DueDate,
    DateTime CreatedAt,
    DateTime UpdatedAt
);
