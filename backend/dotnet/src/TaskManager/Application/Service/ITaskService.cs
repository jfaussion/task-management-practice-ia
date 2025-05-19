using TaskManager.Domain.Models;

namespace TaskManager.Application.Service;

public interface ITaskService
{
    /// <summary>
    /// Get all tasks.
    /// </summary>
    /// <returns>List of all tasks</returns>
    Task<IEnumerable<TaskDomain>> GetAllTasksAsync(CancellationToken cancellationToken);

    /// <summary>
    /// Get tasks by status.
    /// </summary>
    /// <param name="status">Task status</param>
    /// <returns>List of tasks with the given status</returns>
    Task<IEnumerable<TaskDomain>> GetTasksByStatusAsync(string status, CancellationToken cancellationToken);

    /// <summary>
    /// Get tasks by assignee id.
    /// </summary>
    /// <param name="assigneeId">Assignee ID</param>
    /// <returns>List of tasks assigned to the given user</returns>
    Task<IEnumerable<TaskDomain>> GetTasksByAssigneeAsync(Guid assigneeId, CancellationToken cancellationToken);

    /// <summary>
    /// Get a task by ID.
    /// </summary>
    /// <param name="id">Task ID</param>
    /// <returns>Task if found</returns>
    Task<TaskDomain?> GetTaskByIdAsync(Guid id, CancellationToken cancellationToken);

    /// <summary>
    /// Create a new task.
    /// </summary>
    /// <param name="task">Task to create</param>
    /// <returns>Created task</returns>
    Task<TaskDomain> CreateTaskAsync(TaskDomain task, CancellationToken cancellationToken);

    /// <summary>
    /// Update an existing task.
    /// </summary>
    /// <param name="id">TAsk ID</param>
    /// <param name="task">Task data to update</param>
    /// <returns>Updated task</returns>
    Task<TaskDomain> UpdateTaskAsync(Guid id, TaskDomain task, CancellationToken cancellationToken);

    /// <summary>
    /// Assign a task to a user.
    /// </summary>
    /// <param name="taskId">Task ID</param>
    /// <param name="assigneeId">Assignee ID (null to unassign)</param>
    /// <returns>Updated task</returns>
    Task<TaskDomain> AssignTaskAsync(Guid taskId, Guid? assigneeId, CancellationToken cancellationToken);

    /// <summary>
    /// Update the status of a task.
    /// </summary>
    /// <param name="taskId">Task ID</param>
    /// <param name="status">New status</param>
    /// <returns>Updated task</returns>
    Task<TaskDomain> UpdateTaskStatusAsync(Guid taskId, string status, CancellationToken cancellationToken);

    /// <summary>
    /// Estimate the time required to complete a task.
    /// This is a simple decorator method that returns an estimate based on task properties.
    /// </summary>
    /// <param name="taskId">Task ID</param>
    /// <returns>Estimated time in hours</returns>
    Task<double?> EstimateTaskTimeAsync(Guid taskId, CancellationToken cancellationToken);
}
