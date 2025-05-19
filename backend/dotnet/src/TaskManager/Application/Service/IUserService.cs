using TaskManager.Domain.Models;

namespace TaskManager.Application.Service;

public interface IUserService
{
    /// <summary>
    /// Get all users.
    /// </summary>
    /// <returns>List of all users</returns>
    Task<IEnumerable<User>> GetAllUsersAsync(CancellationToken cancellationToken);

    /// <summary>
    /// Get a user by ID.
    /// </summary>
    /// <param name="id">User ID</param>
    /// <returns>User if found</returns>
    Task<User?> GetUserByIdAsync(Guid id, CancellationToken cancellationToken);

    /// <summary>
    /// Get a user by username.
    /// </summary>
    /// <param name="username">Username</param>
    /// <returns>User if found</returns>
    Task<User?> GetUserByUsernameAsync(string username, CancellationToken cancellationToken);

    /// <summary>
    /// Create a new user.
    /// </summary>
    /// <param name="user">User to create</param>
    /// <returns>Created user</returns>
    Task<User> CreateUserAsync(User user, CancellationToken cancellationToken);

    /// <summary>
    /// Update a user.
    /// </summary>
    /// <param name="id">User ID</param>
    /// <param name="user">User data to update</param>
    /// <returns>Updated user</returns>
    Task<User> UpdateUserAsync(Guid id, User user, CancellationToken cancellationToken);

    /// <summary>
    /// Delete a user.
    /// </summary>
    /// <param name="id">User ID</param>
    /// <returns>True if the user was deleted</returns>
    Task<bool> DeleteUserAsync(Guid id, CancellationToken cancellationToken);

    /// <summary>
    /// Check if a username already exists.
    /// </summary>
    /// <param name="username">Username to check</param>
    /// <returns>True if the username exists</returns>
    Task<bool> ExistsByUsernameAsync(string username, CancellationToken cancellationToken);
}
