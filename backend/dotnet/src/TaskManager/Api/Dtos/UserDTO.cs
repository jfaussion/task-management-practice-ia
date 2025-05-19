namespace TaskManager.Api.Dtos;

public record UserDTO(
    Guid Id,
    string Username,
    string? Email,
    string Role,
    DateTime CreatedAt,
    DateTime UpdatedAt
);
