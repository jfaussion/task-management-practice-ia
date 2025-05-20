using TaskManager.Api.Dtos;
using TaskManager.Domain.Models;

namespace TaskManager.Api.Mapper;

public static class UserExtensions
{
    public static UserDTO ToDto(this User user) => new(
        Id: user.Id.GetValueOrDefault(),
        Username: user.Username ?? string.Empty,
        Email: user.Email,
        Role: user.Role ?? string.Empty,
        CreatedAt: user.CreatedAt.GetValueOrDefault(),
        UpdatedAt: user.UpdatedAt.GetValueOrDefault()
    );

    public static IEnumerable<UserDTO> ToDto(this IEnumerable<User> users) => users.Select(u => u.ToDto());

    public static User ToModel(this CreateUserDTO userDto) => new()
    {
        Username = userDto.Username,
        Email = userDto.Email,
        Role = userDto.Role.ToUpper()
    };
}
