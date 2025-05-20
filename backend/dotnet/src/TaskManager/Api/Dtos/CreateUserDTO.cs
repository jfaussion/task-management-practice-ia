namespace TaskManager.Api.Dtos;

public record CreateUserDTO(
    string Username,
    string? Email,
    string Role
);
