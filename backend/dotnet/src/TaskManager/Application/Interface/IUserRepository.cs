using TaskManager.Domain.Models;

namespace TaskManager.Application.Interface;

public interface IUserRepository
{
    Task<IEnumerable<User>> FindAllAsync(CancellationToken cancellationToken);
    Task<User?> FindByIdAsync(Guid id, CancellationToken cancellationToken);
    Task<User?> FindByUsernameAsync(string username, CancellationToken cancellationToken);
    Task<User> SaveAsync(User user, CancellationToken cancellationToken);
    Task<User> UpdateAsync(User user, CancellationToken cancellationToken);
    Task DeleteByIdAsync(User user, CancellationToken cancellationToken);
    Task<bool> ExistsByIdAsync(Guid id, CancellationToken cancellationToken);
    Task<bool> ExistsByUsernameAsync(string username, CancellationToken cancellationToken);
}
