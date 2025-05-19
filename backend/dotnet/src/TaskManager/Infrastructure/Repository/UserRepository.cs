using Microsoft.EntityFrameworkCore;
using System.Threading.Tasks;
using TaskManager.Application.Interface;
using TaskManager.Domain.Models;
using TaskManager.Infrastructure.EFCore;

namespace TaskManager.Infrastructure.Repository;

internal class UserRepository(TaskManagementContext dbContext) : IUserRepository
{
    private readonly TaskManagementContext _dbContext = dbContext;

    public async Task<IEnumerable<User>> FindAllAsync(CancellationToken cancellationToken)
    {
        return await _dbContext.Users.ToListAsync(cancellationToken);
    }

    public async Task<User?> FindByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        return await _dbContext.Users.FindAsync([id], cancellationToken: cancellationToken);
    }

    public async Task<User?> FindByUsernameAsync(string username, CancellationToken cancellationToken)
    {
        return await _dbContext.Users.SingleOrDefaultAsync(predicate: u => u.Username == username, cancellationToken);
    }       

    public async Task<User> SaveAsync(User user, CancellationToken cancellationToken)
    {
        // Set creation and update dates if not already set
        if (user.CreatedAt == null)
        {
            var now = DateTime.UtcNow;
            user.CreatedAt = now;
            user.UpdatedAt = now;
        }
        await _dbContext.Users.AddAsync(user, cancellationToken);
        await _dbContext.SaveChangesAsync(cancellationToken);

        return user;
    }

    public async Task<User> UpdateAsync(User user, CancellationToken cancellationToken)
    {
        // Always update the updatedAt field
        user.UpdatedAt = DateTime.UtcNow;
        _dbContext.Users.Update(user);
        await _dbContext.SaveChangesAsync(cancellationToken);

        return user;
    }

    public async Task DeleteByIdAsync(User user, CancellationToken cancellationToken)
    {
        _dbContext.Users.Remove(user);
        await _dbContext.SaveChangesAsync(cancellationToken);
    }

    public async Task<bool> ExistsByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        return await _dbContext.Users.AnyAsync(predicate: u => u.Id == id, cancellationToken: cancellationToken);
    }

    public async Task<bool> ExistsByUsernameAsync(string username, CancellationToken cancellationToken)
    {
        return await _dbContext.Users.AnyAsync(predicate: u => u.Username == username, cancellationToken: cancellationToken);
    }
}
