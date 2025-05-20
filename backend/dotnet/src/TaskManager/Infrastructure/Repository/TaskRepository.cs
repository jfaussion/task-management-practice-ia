using Microsoft.EntityFrameworkCore;
using TaskManager.Application.Interface;
using TaskManager.Domain.Models;
using TaskManager.Infrastructure.EFCore;

namespace TaskManager.Infrastructure.Repository;

internal class TaskRepository(TaskManagementContext dbContext) : ITaskRepository
{
    private readonly TaskManagementContext _dbContext = dbContext;

    public async Task<IEnumerable<TaskDomain>> FindAllAsync(CancellationToken cancellationToken)
    {        
        return await _dbContext.Tasks.AsNoTracking().Include(task => task.Assignee).ToListAsync(cancellationToken);
    }

    public async Task<IEnumerable<TaskDomain>> FindByAssigneeIdAsync(Guid assigneeId, CancellationToken cancellationToken)
    {
        return await _dbContext.Tasks.AsNoTracking().Where(t => t.AssigneeId == assigneeId).Include(task => task.Assignee).ToListAsync(cancellationToken);
    }

    public async Task<IEnumerable<TaskDomain>> FindByStatusAsync(string status, CancellationToken cancellationToken)
    {
        return await _dbContext.Tasks.AsNoTracking().Where(t => t.Status == status).Include(task => task.Assignee).ToListAsync(cancellationToken);
    }

    public async Task<TaskDomain?> FindByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        return await _dbContext.Tasks.AsNoTracking().Include(task => task.Assignee).SingleOrDefaultAsync(t => t.Id == id, cancellationToken: cancellationToken); //better
        
        //var task = await _dbContext.Tasks.FindAsync([id], cancellationToken: cancellationToken); 
        //if(task != null)
        //{
        //    await _dbContext.Entry(task).Reference(t => t.Assignee).LoadAsync();
        //}
        //return task;
    }
   
    public async Task<TaskDomain?> FindByTitleAndAssigneeIdAsync(string title, Guid assigneeId, CancellationToken cancellationToken)
    {
        return await _dbContext.Tasks.AsNoTracking().Include(task => task.Assignee).SingleOrDefaultAsync(predicate: t => t.Title == title && t.AssigneeId == assigneeId, cancellationToken);
    }      

    public async Task<TaskDomain> SaveAsync(TaskDomain task, CancellationToken cancellationToken)
    {
        // Set creation and update dates if not already set
        if (task.CreatedAt == null)
        {
            var now = DateTime.UtcNow;
            task.CreatedAt = now;
            task.UpdatedAt = now;
        }
        await _dbContext.Tasks.AddAsync(task, cancellationToken);
        await _dbContext.SaveChangesAsync(cancellationToken);
        
        return task;
    }

    public async Task<TaskDomain> UpdateAsync(TaskDomain task, CancellationToken cancellationToken)
    {
        // Always update the updatedAt field
        task.UpdatedAt = DateTime.UtcNow;
        _dbContext.Tasks.Update(task);
        await _dbContext.SaveChangesAsync(cancellationToken);

        return task;
    }

    public async Task<bool> ExistsByIdAsync(Guid id, CancellationToken cancellationToken)
    {
        return await _dbContext.Tasks.AnyAsync(predicate: t => t.Id == id, cancellationToken: cancellationToken);
    }

    public async Task<bool> ExistsByTitleAndAssigneeIdAsync(string title, Guid assigneeId, CancellationToken cancellationToken)
    {
        return await _dbContext.Tasks.AnyAsync(predicate: t => t.Title == title && t.AssigneeId == assigneeId, cancellationToken: cancellationToken);
    }
}
