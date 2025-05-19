using Asp.Versioning;
using Microsoft.AspNetCore.Mvc;
using TaskManager.Api.Dtos;
using TaskManager.Api.Mapper;
using TaskManager.Application.Service;

namespace TaskManager.Api.Controllers;

[ApiVersion("1.0")]
public class TasksController(ITaskService taskService) : BaseController
{
    private readonly ITaskService _taskService = taskService;

    // GET: api/v1/tasks
    [HttpGet]
    public async Task<ActionResult<IEnumerable<TaskDTO>>> Get(CancellationToken cancellationToken)
    {
        var tasks = await _taskService.GetAllTasksAsync(cancellationToken);
        return Ok(tasks.ToDto());
    }

    // GET api/v1/tasks/5
    [HttpGet("{id}")]
    public async Task<ActionResult<TaskDTO>> Get(Guid id, CancellationToken cancellationToken)
    {
        var task = await _taskService.GetTaskByIdAsync(id, cancellationToken);
        if (task is null)
        {
            return NotFound();
        }
        return Ok(task.ToDto());
    }

    // POST api/v1/tasks
    [HttpPost]
    public async Task<ActionResult<TaskDTO>> Post([FromBody] CreateTaskDTO createTaskDTO, CancellationToken cancellationToken)
    {
        var task = createTaskDTO.ToModel();
        var createdTask = await _taskService.CreateTaskAsync(task, cancellationToken);
        return CreatedAtAction(nameof(Post), new { id = createdTask.Id }, createdTask.ToDto());
    }

    // PUT api/v1/tasks/5
    [HttpPut("{id}")]
    public async Task<ActionResult<TaskDTO>> Put(Guid id, [FromBody] CreateTaskDTO createTaskDTO, CancellationToken cancellationToken)
    {
        var task = createTaskDTO.ToModel();
        var updatedTask = await _taskService.UpdateTaskAsync(id, task, cancellationToken);
        return Ok(updatedTask.ToDto());
    }

    // PUT api/v1/tasks/5/assign
    [HttpPut("{id}/assign")]
    public async Task<ActionResult<TaskDTO>> PutAssignTask(Guid id, [FromQuery] Guid assigneeId, CancellationToken cancellationToken)
    {
        var task = await _taskService.AssignTaskAsync(id, assigneeId, cancellationToken);
        return Ok(task.ToDto());
    }

    // PUT api/v1/tasks/5/status
    [HttpPut("{id}/status")]
    public async Task<ActionResult<TaskDTO>> PutTaskStatus(Guid id, [FromQuery] string status, CancellationToken cancellationToken)
    {
        var task = await _taskService.UpdateTaskStatusAsync(id, status, cancellationToken);
        return Ok(task.ToDto());
    }

    // GET api/v1/tasks/5/estimate
    [HttpGet("{id}/estimate")]
    public async Task<ActionResult<double>> GetEstimateTaskTime(Guid id, CancellationToken cancellationToken)
    {
        var estimatedTime = await _taskService.EstimateTaskTimeAsync(id, cancellationToken);
        return Ok(estimatedTime);
    }
}
