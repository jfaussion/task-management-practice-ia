using TaskManager.Application.Interface;
using TaskManager.Domain.Models;

namespace TaskManager.Application.Service;

internal class UserService(IUserRepository userRepository) : IUserService
{
    private readonly IUserRepository _userRepository = userRepository;

    public async Task<IEnumerable<User>> GetAllUsersAsync(CancellationToken cancellationToken)
    {
        return await _userRepository.FindAllAsync(cancellationToken);
    }

    public async Task<User?> GetUserByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        return await _userRepository.FindByIdAsync(id, cancellationToken);
    }

    public async Task<User?> GetUserByUsernameAsync(string username, CancellationToken cancellationToken)
    {
        return await _userRepository.FindByUsernameAsync(username, cancellationToken);
    }

    public async Task<User> CreateUserAsync(User user, CancellationToken cancellationToken)
    {
        // Ensure new user has no ID (will be generated)
        user.Id = null;

        // Check if username provide and unique
        if (!string.IsNullOrWhiteSpace(user.Username) &&
            await _userRepository.ExistsByUsernameAsync(user.Username, cancellationToken))
        {
            throw new InvalidOperationException("A user with this username already exists");
        }
        return await _userRepository.SaveAsync(user, cancellationToken);
    }

    public async Task<User> UpdateUserAsync(Guid id, User user, CancellationToken cancellationToken)
    {
        // Check if user exists
        if (!await _userRepository.ExistsByIdAsync(id, cancellationToken))
        {
            throw new InvalidOperationException("User not found with ID: " + id);
        }

        var originalUser = await _userRepository.FindByIdAsync(id, cancellationToken);
        if (originalUser is not null)
        {
            // Check for username uniqueness if it changed
            if (user.Username != null &&
                (!user.Username.Equals(originalUser.Username)) &&
                await ExistsByUsernameAsync(user.Username, cancellationToken))
            {
                throw new InvalidOperationException("A user with this username already exist");
            }

            UpdateUserProperties(originalUser, user);
            return await _userRepository.UpdateAsync(originalUser, cancellationToken);
        }
        else
        {
            return await _userRepository.SaveAsync(user, cancellationToken);
        }
    }

    private static void UpdateUserProperties(User originalUser, User user)
    {
        originalUser.Username = user.Username;
        originalUser.Email = user.Email;
        originalUser.Role = user.Role;
    }

    public async Task<bool> DeleteUserAsync(Guid id, CancellationToken cancellationToken)
    {
        var user = await _userRepository.FindByIdAsync(id, cancellationToken);
        if (user is null)
            return false;

        await _userRepository.DeleteByIdAsync(user, cancellationToken);
        return true;
    }

    public async Task<bool> ExistsByUsernameAsync(string username, CancellationToken cancellationToken)
    {
        return await _userRepository.ExistsByUsernameAsync(username, cancellationToken);
    }
}
