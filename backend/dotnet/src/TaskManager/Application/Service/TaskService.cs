using TaskManager.Application.Interface;
using TaskManager.Domain.Models;

namespace TaskManager.Application.Service;

internal class TaskService(ITaskRepository taskRepository, IUserRepository userRepository) : ITaskService
{
    private readonly ITaskRepository _taskRepository = taskRepository;
    private readonly IUserRepository _userRepository = userRepository;

    public async Task<IEnumerable<TaskDomain>> GetAllTasksAsync(CancellationToken cancellationToken)
    {
        return await _taskRepository.FindAllAsync(cancellationToken);
    }
    public async Task<IEnumerable<TaskDomain>> GetTasksByStatusAsync(string status, CancellationToken cancellationToken)
    {
        return await _taskRepository.FindByStatusAsync(status, cancellationToken);
    }

    public async Task<IEnumerable<TaskDomain>> GetTasksByAssigneeAsync(Guid assigneeId, CancellationToken cancellationToken)
    {
        // Verify that the user exists
        if (!await _userRepository.ExistsByIdAsync(assigneeId, cancellationToken))
        {
            throw new ArgumentException("User not found with ID: " + assigneeId);
        }

        return await _taskRepository.FindByAssigneeIdAsync(assigneeId, cancellationToken);
    }

    public async Task<TaskDomain?> GetTaskByIdAsync(Guid Id, CancellationToken cancellationToken)
    {
        return await _taskRepository.FindByIdAsync(Id, cancellationToken);
    }

    public async Task<TaskDomain> CreateTaskAsync(TaskDomain task, CancellationToken cancellationToken)
    {
        // Ensure new task has no ID (will be generated)
        task.Id = null;

        // Verify that the assignee is provided
        if (!task.AssigneeId.HasValue)
        {
            return await _taskRepository.SaveAsync(task, cancellationToken);
        }
                
        var assigneeId = task.AssigneeId.Value;
        // Verify that the assignee exists
        if (!await _userRepository.ExistsByIdAsync(assigneeId, cancellationToken))
        {
            throw new ArgumentException("Assignee not found with ID: " + assigneeId);
        }

        // Check for title uniqueness per assignee
        if (!string.IsNullOrWhiteSpace(task.Title) &&
            await _taskRepository.ExistsByTitleAndAssigneeIdAsync(task.Title, assigneeId, cancellationToken))
        {
            throw new ArgumentException("A task with this title already exists for this user");
        }

        return await _taskRepository.SaveAsync(task, cancellationToken);
    }

    public async Task<TaskDomain> UpdateTaskAsync(Guid id, TaskDomain task, CancellationToken cancellationToken)
    {
        // Check if task exists
        if (!await _taskRepository.ExistsByIdAsync(id, cancellationToken))
        {
            throw new ArgumentException("Task not found with ID: " + id);
        }

        // Verify that the assignee exists if provided
        if (task.AssigneeId.HasValue && ! await _userRepository.ExistsByIdAsync(task.AssigneeId.Value, cancellationToken))
        {
            throw new ArgumentException("Assignee not found with ID: " + task.AssigneeId);
        }

        // Get the original task to check if title or assignee changed
        var originalTask = await _taskRepository.FindByIdAsync(id, cancellationToken);
        if (originalTask is not null)
        {
            // Check for title uniqueness per assignee if title or assignee changed
            if (task.AssigneeId.HasValue && task.Title != null &&
                (task.AssigneeId != originalTask.AssigneeId || !task.Title.Equals(originalTask.Title)) &&
                await _taskRepository.ExistsByTitleAndAssigneeIdAsync(task.Title, task.AssigneeId.Value, cancellationToken))
            {
                throw new ArgumentException("A task with this title already exists for this user");
            }

            await UpdateTaskPropertiesAsync(originalTask, task, cancellationToken);
            return await _taskRepository.UpdateAsync(originalTask, cancellationToken);
        }
        else
        {
            return await _taskRepository.SaveAsync(task, cancellationToken);
        }
    }

    private async Task UpdateTaskPropertiesAsync(TaskDomain originalTask, TaskDomain task, CancellationToken cancellationToken)
    {
        // Update the task properties
        originalTask.Title = task.Title;
        originalTask.Description = task.Description;
        originalTask.Priority = task.Priority;
        originalTask.Status = task.Status;
        originalTask.DueDate = task.DueDate;
        if (task.AssigneeId != originalTask.AssigneeId)
        {
            if (task.AssigneeId.HasValue)
            {
                var assignee = await _userRepository.FindByIdAsync(task.AssigneeId.Value, cancellationToken);
                originalTask.AssigneeId = task.AssigneeId.Value;
                originalTask.Assignee = assignee;
            }
            else
            {
                originalTask.AssigneeId = null;
                originalTask.Assignee = null;
            }
        }
    }

    public async Task<TaskDomain> AssignTaskAsync(Guid taskId, Guid? assigneeId, CancellationToken cancellationToken)
    {
        // Get the task
        var task = await _taskRepository.FindByIdAsync(taskId, cancellationToken) ?? throw new ArgumentException("Task not found with ID: " + taskId);

        // Verify that the assignee exists if provided
        if (assigneeId.HasValue && !await _userRepository.ExistsByIdAsync(assigneeId.Value, cancellationToken))
        {
            throw new ArgumentException("Assignee not found with ID: " + assigneeId);
        }

        // Check for title uniqueness per assignee if assignee changed
        if (assigneeId.HasValue && task.Title != null &&
            !assigneeId.Value.Equals(task.AssigneeId) &&
            await _taskRepository.ExistsByTitleAndAssigneeIdAsync(task.Title, assigneeId.Value, cancellationToken))
        {
            throw new ArgumentException("A task with this title already exists for this user");
        }

        // Set the new assignee
        task.AssigneeId = assigneeId;

        return await _taskRepository.UpdateAsync(task, cancellationToken);
    }

    public async Task<TaskDomain> UpdateTaskStatusAsync(Guid taskId, string status, CancellationToken cancellationToken)
    {
        // Get the task
        var task = await _taskRepository.FindByIdAsync(taskId, cancellationToken) ?? throw new ArgumentException("Task not found with ID: " + taskId);

        // Validate status (should be done with an enum in a real application)
        if (!IsValidStatus(status))
        {
            throw new ArgumentException("Invalid status: " + status);
        }

        // Set the new status
        task.Status = status;

        return await _taskRepository.UpdateAsync(task, cancellationToken);
    }

    public async Task<double?> EstimateTaskTimeAsync(Guid taskId, CancellationToken cancellationToken)
    {
        // Get the task
        var task = await _taskRepository.FindByIdAsync(taskId, cancellationToken) ?? throw new ArgumentException("Task not found with ID: " + taskId);

        // Simple estimation logic based on task properties
        double baseHours = 2.0; // Default base hours

        // Adjust based on priority
        if (task.Priority is not null)
        {
            switch (task.Priority)
            {
                case "HIGH":
                    baseHours *= 1.5;
                    break;
                case "LOW":
                    baseHours *= 0.75;
                    break;
                default: // MEDIUM or other
                    // Keep base hours
                    break;
            }
        }

        // Adjust based on description length (if available)
        if (!string.IsNullOrWhiteSpace(task.Description))
        {
            // Longer descriptions might indicate more complex tasks
            int wordCount = task.Description.Split("\\s+").Length;
            baseHours += (wordCount / 50) * 0.5; // Add 0.5 hours per 50 words
        }

        // Adjust based on status
        if ("IN_PROGRESS".Equals(task.Status))
        {
            baseHours *= 0.7; // 30% already done
        }
        else if ("DONE".Equals(task.Status))
        {
            baseHours = 0; // Already completed
        }

        return Math.Max(0.25, baseHours); // Minimum 15 minutes (0.25 hours)
    }

    /// <summary>
    /// Validate task status.
    /// </summary>
    /// <param name="status">Status to validate</param>
    /// <returns>true if the status is valid</returns>
    private static bool IsValidStatus(String status)
    {
        return "TODO".Equals(status) || "IN_PROGRESS".Equals(status) || "DONE".Equals(status);
    }
}
