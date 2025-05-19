using TaskManager.Domain.Models;

namespace TaskManager.Application.Interface;

public interface ITaskRepository
{
    Task<IEnumerable<TaskDomain>> FindAllAsync(CancellationToken cancellationToken);
    Task<IEnumerable<TaskDomain>> FindByStatusAsync(string status, CancellationToken cancellationToken);
    Task<IEnumerable<TaskDomain>> FindByAssigneeIdAsync(Guid assigneeId, CancellationToken cancellationToken);
    Task<TaskDomain?> FindByIdAsync(Guid id, CancellationToken cancellationToken);
    Task<TaskDomain?> FindByTitleAndAssigneeIdAsync(string title, Guid assigneeId, CancellationToken cancellationToken);
    Task<TaskDomain> SaveAsync(TaskDomain task, CancellationToken cancellationToken);
    Task<TaskDomain> UpdateAsync(TaskDomain task, CancellationToken cancellationToken);
    Task<bool> ExistsByIdAsync(Guid id, CancellationToken cancellationToken);
    Task<bool> ExistsByTitleAndAssigneeIdAsync(string title, Guid assigneeId, CancellationToken cancellationToken);
}
