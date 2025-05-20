using TaskManager.Api.Dtos;
using TaskManager.Domain.Models;

namespace TaskManager.Api.Mapper;

public static class TaskExtensions
{
    public static TaskDTO ToDto(this TaskDomain task) => new(
        Id : task.Id.GetValueOrDefault(),
        Title: task.Title ?? string.Empty,
        Description: task.Description ?? string.Empty,
        Status : task.Status ?? string.Empty,
        Priority : task.Priority,
        AssigneeId: task.AssigneeId,
        Assignee: task.Assignee?.ToDto(),
        DueDate: task.DueDate,
        CreatedAt: task.CreatedAt.GetValueOrDefault(),
        UpdatedAt: task.UpdatedAt.GetValueOrDefault()
    );

    public static IEnumerable<TaskDTO> ToDto(this IEnumerable<TaskDomain> tasks) => tasks.Select(t => t.ToDto());

    public static TaskDomain ToModel(this CreateTaskDTO createTaskDTO) => new()
    {
        Title = createTaskDTO.Title,
        Description = createTaskDTO.Description,
        Status = createTaskDTO.Status.ToUpper(),
        Priority = createTaskDTO.Priority?.ToUpper(),
        AssigneeId = createTaskDTO.AssigneeId,
        DueDate = createTaskDTO.DueDate
    };
}
