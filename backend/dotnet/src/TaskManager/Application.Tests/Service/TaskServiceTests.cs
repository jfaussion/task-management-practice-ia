using FluentAssertions;
using Moq;
using TaskManager.Application.Interface;
using TaskManager.Application.Service;
using TaskManager.Domain.Models;

namespace TaskManager.Application.Tests.Service;

public class TaskServiceTests
{
    private readonly Mock<ITaskRepository> _taskRepositoryMock;
    private readonly Mock<IUserRepository> _userRepositoryMock;
    private readonly TaskService _sut;

    public TaskServiceTests()
    {
        _taskRepositoryMock = new Mock<ITaskRepository>();
        _userRepositoryMock = new Mock<IUserRepository>();
        _sut = new TaskService(_taskRepositoryMock.Object, _userRepositoryMock.Object);
    }

    #region GetAllTasksAsync

    [Fact]
    public async Task GetAllTasksAsync_ShouldReturnAllTasks()
    {
        // Arrange
        var tasks = new List<TaskDomain>
        {
            new() { Id = Guid.NewGuid(), Title = "Task 1" },
            new() { Id = Guid.NewGuid(), Title = "Task 2" }
        };
        _taskRepositoryMock.Setup(r => r.FindAllAsync(It.IsAny<CancellationToken>()))
            .ReturnsAsync(tasks);

        // Act
        var result = await _sut.GetAllTasksAsync(CancellationToken.None);

        // Assert
        result.Should().BeEquivalentTo(tasks);
        _taskRepositoryMock.Verify(r => r.FindAllAsync(It.IsAny<CancellationToken>()), Times.Once);
    }

    [Fact]
    public async Task GetAllTasksAsync_WhenNoTasks_ShouldReturnEmptyList()
    {
        // Arrange
        _taskRepositoryMock.Setup(r => r.FindAllAsync(It.IsAny<CancellationToken>()))
            .ReturnsAsync(new List<TaskDomain>());

        // Act
        var result = await _sut.GetAllTasksAsync(CancellationToken.None);

        // Assert
        result.Should().BeEmpty();
    }

    #endregion

    #region GetTasksByStatusAsync

    [Fact]
    public async Task GetTasksByStatusAsync_ShouldReturnFilteredTasks()
    {
        // Arrange
        var tasks = new List<TaskDomain>
        {
            new() { Id = Guid.NewGuid(), Title = "Task 1", Status = "TODO" }
        };
        _taskRepositoryMock.Setup(r => r.FindByStatusAsync("TODO", It.IsAny<CancellationToken>()))
            .ReturnsAsync(tasks);

        // Act
        var result = await _sut.GetTasksByStatusAsync("TODO", CancellationToken.None);

        // Assert
        result.Should().BeEquivalentTo(tasks);
    }

    #endregion

    #region GetTasksByAssigneeAsync

    [Fact]
    public async Task GetTasksByAssigneeAsync_WhenUserExists_ShouldReturnTasks()
    {
        // Arrange
        var assigneeId = Guid.NewGuid();
        var tasks = new List<TaskDomain>
        {
            new() { Id = Guid.NewGuid(), Title = "Task 1", AssigneeId = assigneeId }
        };
        _userRepositoryMock.Setup(r => r.ExistsByIdAsync(assigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);
        _taskRepositoryMock.Setup(r => r.FindByAssigneeIdAsync(assigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(tasks);

        // Act
        var result = await _sut.GetTasksByAssigneeAsync(assigneeId, CancellationToken.None);

        // Assert
        result.Should().BeEquivalentTo(tasks);
    }

    [Fact]
    public async Task GetTasksByAssigneeAsync_WhenUserNotFound_ShouldThrowArgumentException()
    {
        // Arrange
        var assigneeId = Guid.NewGuid();
        _userRepositoryMock.Setup(r => r.ExistsByIdAsync(assigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(false);

        // Act
        var act = () => _sut.GetTasksByAssigneeAsync(assigneeId, CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<ArgumentException>()
            .WithMessage($"User not found with ID: {assigneeId}");
    }

    #endregion

    #region GetTaskByIdAsync

    [Fact]
    public async Task GetTaskByIdAsync_WhenTaskExists_ShouldReturnTask()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var task = new TaskDomain { Id = taskId, Title = "Test Task" };
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(task);

        // Act
        var result = await _sut.GetTaskByIdAsync(taskId, CancellationToken.None);

        // Assert
        result.Should().BeEquivalentTo(task);
    }

    [Fact]
    public async Task GetTaskByIdAsync_WhenTaskNotFound_ShouldReturnNull()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync((TaskDomain?)null);

        // Act
        var result = await _sut.GetTaskByIdAsync(taskId, CancellationToken.None);

        // Assert
        result.Should().BeNull();
    }

    #endregion

    #region CreateTaskAsync

    [Fact]
    public async Task CreateTaskAsync_WithoutAssignee_ShouldResetIdAndSave()
    {
        // Arrange
        var originalId = Guid.NewGuid();
        var task = new TaskDomain { Id = originalId, Title = "New Task", Description = "Description" };
        TaskDomain? capturedTask = null;

        _taskRepositoryMock.Setup(r => r.SaveAsync(It.IsAny<TaskDomain>(), It.IsAny<CancellationToken>()))
            .Callback<TaskDomain, CancellationToken>((t, _) => capturedTask = t)
            .ReturnsAsync((TaskDomain t, CancellationToken _) => t);

        // Act
        await _sut.CreateTaskAsync(task, CancellationToken.None);

        // Assert
        capturedTask.Should().NotBeNull();
        capturedTask!.Id.Should().BeNull("ID should be reset to null before saving");
        capturedTask.Title.Should().Be("New Task");
        capturedTask.Description.Should().Be("Description");
        _taskRepositoryMock.Verify(r => r.SaveAsync(It.IsAny<TaskDomain>(), It.IsAny<CancellationToken>()), Times.Once);
    }

    [Fact]
    public async Task CreateTaskAsync_WithValidAssignee_ShouldVerifyAssigneeAndSave()
    {
        // Arrange
        var assigneeId = Guid.NewGuid();
        var task = new TaskDomain { Id = Guid.NewGuid(), Title = "New Task", AssigneeId = assigneeId };
        TaskDomain? capturedTask = null;

        _userRepositoryMock.Setup(r => r.ExistsByIdAsync(assigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);
        _taskRepositoryMock.Setup(r => r.ExistsByTitleAndAssigneeIdAsync("New Task", assigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(false);
        _taskRepositoryMock.Setup(r => r.SaveAsync(It.IsAny<TaskDomain>(), It.IsAny<CancellationToken>()))
            .Callback<TaskDomain, CancellationToken>((t, _) => capturedTask = t)
            .ReturnsAsync((TaskDomain t, CancellationToken _) => t);

        // Act
        await _sut.CreateTaskAsync(task, CancellationToken.None);

        // Assert
        _userRepositoryMock.Verify(r => r.ExistsByIdAsync(assigneeId, It.IsAny<CancellationToken>()), Times.Once);
        _taskRepositoryMock.Verify(r => r.ExistsByTitleAndAssigneeIdAsync("New Task", assigneeId, It.IsAny<CancellationToken>()), Times.Once);
        capturedTask.Should().NotBeNull();
        capturedTask!.Id.Should().BeNull();
        capturedTask.AssigneeId.Should().Be(assigneeId);
    }

    [Fact]
    public async Task CreateTaskAsync_WhenAssigneeNotFound_ShouldThrowArgumentException()
    {
        // Arrange
        var assigneeId = Guid.NewGuid();
        var task = new TaskDomain { Title = "New Task", AssigneeId = assigneeId };
        _userRepositoryMock.Setup(r => r.ExistsByIdAsync(assigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(false);

        // Act
        var act = () => _sut.CreateTaskAsync(task, CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<ArgumentException>()
            .WithMessage($"Assignee not found with ID: {assigneeId}");
    }

    [Fact]
    public async Task CreateTaskAsync_WhenDuplicateTitleForAssignee_ShouldThrowArgumentException()
    {
        // Arrange
        var assigneeId = Guid.NewGuid();
        var task = new TaskDomain { Title = "Duplicate Title", AssigneeId = assigneeId };

        _userRepositoryMock.Setup(r => r.ExistsByIdAsync(assigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);
        _taskRepositoryMock.Setup(r => r.ExistsByTitleAndAssigneeIdAsync("Duplicate Title", assigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);

        // Act
        var act = () => _sut.CreateTaskAsync(task, CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<ArgumentException>()
            .WithMessage("A task with this title already exists for this user");
    }

    #endregion

    #region UpdateTaskAsync

    [Fact]
    public async Task UpdateTaskAsync_WhenTaskExists_ShouldUpdateProperties()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var originalTask = new TaskDomain
        {
            Id = taskId,
            Title = "Original",
            Description = "Old Description",
            Status = "TODO",
            Priority = "LOW"
        };
        var updateTask = new TaskDomain
        {
            Title = "Updated",
            Description = "New Description",
            Status = "IN_PROGRESS",
            Priority = "HIGH"
        };
        TaskDomain? capturedTask = null;

        _taskRepositoryMock.Setup(r => r.ExistsByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(originalTask);
        _taskRepositoryMock.Setup(r => r.UpdateAsync(It.IsAny<TaskDomain>(), It.IsAny<CancellationToken>()))
            .Callback<TaskDomain, CancellationToken>((t, _) => capturedTask = t)
            .ReturnsAsync((TaskDomain t, CancellationToken _) => t);

        // Act
        await _sut.UpdateTaskAsync(taskId, updateTask, CancellationToken.None);

        // Assert
        capturedTask.Should().NotBeNull();
        capturedTask!.Id.Should().Be(taskId, "original task ID should be preserved");
        capturedTask.Title.Should().Be("Updated");
        capturedTask.Description.Should().Be("New Description");
        capturedTask.Status.Should().Be("IN_PROGRESS");
        capturedTask.Priority.Should().Be("HIGH");
        _taskRepositoryMock.Verify(r => r.UpdateAsync(It.IsAny<TaskDomain>(), It.IsAny<CancellationToken>()), Times.Once);
    }

    [Fact]
    public async Task UpdateTaskAsync_WhenTaskNotFound_ShouldThrowArgumentException()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var task = new TaskDomain { Title = "Update" };
        _taskRepositoryMock.Setup(r => r.ExistsByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(false);

        // Act
        var act = () => _sut.UpdateTaskAsync(taskId, task, CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<ArgumentException>()
            .WithMessage($"Task not found with ID: {taskId}");
    }

    [Fact]
    public async Task UpdateTaskAsync_WhenAssigneeNotFound_ShouldThrowArgumentException()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var assigneeId = Guid.NewGuid();
        var task = new TaskDomain { Title = "Update", AssigneeId = assigneeId };

        _taskRepositoryMock.Setup(r => r.ExistsByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);
        _userRepositoryMock.Setup(r => r.ExistsByIdAsync(assigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(false);

        // Act
        var act = () => _sut.UpdateTaskAsync(taskId, task, CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<ArgumentException>()
            .WithMessage($"Assignee not found with ID: {assigneeId}");
    }

    [Fact]
    public async Task UpdateTaskAsync_WhenDuplicateTitleForNewAssignee_ShouldThrowArgumentException()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var originalAssigneeId = Guid.NewGuid();
        var newAssigneeId = Guid.NewGuid();
        var originalTask = new TaskDomain { Id = taskId, Title = "Task", AssigneeId = originalAssigneeId };
        var updateTask = new TaskDomain { Title = "Task", AssigneeId = newAssigneeId };

        _taskRepositoryMock.Setup(r => r.ExistsByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);
        _userRepositoryMock.Setup(r => r.ExistsByIdAsync(newAssigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(originalTask);
        _taskRepositoryMock.Setup(r => r.ExistsByTitleAndAssigneeIdAsync("Task", newAssigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);

        // Act
        var act = () => _sut.UpdateTaskAsync(taskId, updateTask, CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<ArgumentException>()
            .WithMessage("A task with this title already exists for this user");
    }

    #endregion

    #region AssignTaskAsync

    [Fact]
    public async Task AssignTaskAsync_WhenValid_ShouldSetAssigneeIdAndUpdate()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var assigneeId = Guid.NewGuid();
        var task = new TaskDomain { Id = taskId, Title = "Task", AssigneeId = null };
        TaskDomain? capturedTask = null;

        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(task);
        _userRepositoryMock.Setup(r => r.ExistsByIdAsync(assigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);
        _taskRepositoryMock.Setup(r => r.ExistsByTitleAndAssigneeIdAsync("Task", assigneeId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(false);
        _taskRepositoryMock.Setup(r => r.UpdateAsync(It.IsAny<TaskDomain>(), It.IsAny<CancellationToken>()))
            .Callback<TaskDomain, CancellationToken>((t, _) => capturedTask = t)
            .ReturnsAsync((TaskDomain t, CancellationToken _) => t);

        // Act
        await _sut.AssignTaskAsync(taskId, assigneeId, CancellationToken.None);

        // Assert
        capturedTask.Should().NotBeNull();
        capturedTask!.AssigneeId.Should().Be(assigneeId);
        _userRepositoryMock.Verify(r => r.ExistsByIdAsync(assigneeId, It.IsAny<CancellationToken>()), Times.Once);
        _taskRepositoryMock.Verify(r => r.UpdateAsync(It.IsAny<TaskDomain>(), It.IsAny<CancellationToken>()), Times.Once);
    }

    [Fact]
    public async Task AssignTaskAsync_WhenTaskNotFound_ShouldThrowArgumentException()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync((TaskDomain?)null);

        // Act
        var act = () => _sut.AssignTaskAsync(taskId, Guid.NewGuid(), CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<ArgumentException>()
            .WithMessage($"Task not found with ID: {taskId}");
    }

    [Fact]
    public async Task AssignTaskAsync_WithNullAssignee_ShouldSetAssigneeToNull()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var originalAssigneeId = Guid.NewGuid();
        var task = new TaskDomain { Id = taskId, Title = "Task", AssigneeId = originalAssigneeId };
        TaskDomain? capturedTask = null;

        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(task);
        _taskRepositoryMock.Setup(r => r.UpdateAsync(It.IsAny<TaskDomain>(), It.IsAny<CancellationToken>()))
            .Callback<TaskDomain, CancellationToken>((t, _) => capturedTask = t)
            .ReturnsAsync((TaskDomain t, CancellationToken _) => t);

        // Act
        await _sut.AssignTaskAsync(taskId, null, CancellationToken.None);

        // Assert
        capturedTask.Should().NotBeNull();
        capturedTask!.AssigneeId.Should().BeNull();
        _userRepositoryMock.Verify(r => r.ExistsByIdAsync(It.IsAny<Guid>(), It.IsAny<CancellationToken>()), Times.Never);
    }

    #endregion

    #region UpdateTaskStatusAsync

    [Theory]
    [InlineData("TODO")]
    [InlineData("IN_PROGRESS")]
    [InlineData("DONE")]
    public async Task UpdateTaskStatusAsync_WithValidStatus_ShouldSetStatusAndUpdate(string status)
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var task = new TaskDomain { Id = taskId, Title = "Task", Status = "TODO" };
        TaskDomain? capturedTask = null;

        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(task);
        _taskRepositoryMock.Setup(r => r.UpdateAsync(It.IsAny<TaskDomain>(), It.IsAny<CancellationToken>()))
            .Callback<TaskDomain, CancellationToken>((t, _) => capturedTask = t)
            .ReturnsAsync((TaskDomain t, CancellationToken _) => t);

        // Act
        await _sut.UpdateTaskStatusAsync(taskId, status, CancellationToken.None);

        // Assert
        capturedTask.Should().NotBeNull();
        capturedTask!.Status.Should().Be(status);
        _taskRepositoryMock.Verify(r => r.UpdateAsync(It.IsAny<TaskDomain>(), It.IsAny<CancellationToken>()), Times.Once);
    }

    [Fact]
    public async Task UpdateTaskStatusAsync_WithInvalidStatus_ShouldThrowArgumentException()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var task = new TaskDomain { Id = taskId, Title = "Task" };
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(task);

        // Act
        var act = () => _sut.UpdateTaskStatusAsync(taskId, "INVALID", CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<ArgumentException>()
            .WithMessage("Invalid status: INVALID");
    }

    [Fact]
    public async Task UpdateTaskStatusAsync_WhenTaskNotFound_ShouldThrowArgumentException()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync((TaskDomain?)null);

        // Act
        var act = () => _sut.UpdateTaskStatusAsync(taskId, "TODO", CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<ArgumentException>()
            .WithMessage($"Task not found with ID: {taskId}");
    }

    #endregion

    #region EstimateTaskTimeAsync

    [Fact]
    public async Task EstimateTaskTimeAsync_WithDefaultTask_ShouldReturnBaseHours()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var task = new TaskDomain { Id = taskId, Title = "Task", Priority = "MEDIUM", Status = "TODO" };
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(task);

        // Act
        var result = await _sut.EstimateTaskTimeAsync(taskId, CancellationToken.None);

        // Assert
        result.Should().Be(2.0); // Base hours
    }

    [Fact]
    public async Task EstimateTaskTimeAsync_WithHighPriority_ShouldReturn1_5xBaseHours()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var task = new TaskDomain { Id = taskId, Title = "Task", Priority = "HIGH", Status = "TODO" };
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(task);

        // Act
        var result = await _sut.EstimateTaskTimeAsync(taskId, CancellationToken.None);

        // Assert
        result.Should().Be(3.0); // 2.0 * 1.5
    }

    [Fact]
    public async Task EstimateTaskTimeAsync_WithLowPriority_ShouldReturn0_75xBaseHours()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var task = new TaskDomain { Id = taskId, Title = "Task", Priority = "LOW", Status = "TODO" };
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(task);

        // Act
        var result = await _sut.EstimateTaskTimeAsync(taskId, CancellationToken.None);

        // Assert
        result.Should().Be(1.5); // 2.0 * 0.75
    }

    [Fact]
    public async Task EstimateTaskTimeAsync_WhenInProgress_ShouldReturn0_7xHours()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var task = new TaskDomain { Id = taskId, Title = "Task", Priority = "MEDIUM", Status = "IN_PROGRESS" };
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(task);

        // Act
        var result = await _sut.EstimateTaskTimeAsync(taskId, CancellationToken.None);

        // Assert
        result.Should().Be(1.4); // 2.0 * 0.7
    }

    [Fact]
    public async Task EstimateTaskTimeAsync_WhenDone_ShouldReturnMinimum()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        var task = new TaskDomain { Id = taskId, Title = "Task", Status = "DONE" };
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(task);

        // Act
        var result = await _sut.EstimateTaskTimeAsync(taskId, CancellationToken.None);

        // Assert
        result.Should().Be(0.25); // Minimum 15 minutes
    }

    [Fact]
    public async Task EstimateTaskTimeAsync_WhenTaskNotFound_ShouldThrowArgumentException()
    {
        // Arrange
        var taskId = Guid.NewGuid();
        _taskRepositoryMock.Setup(r => r.FindByIdAsync(taskId, It.IsAny<CancellationToken>()))
            .ReturnsAsync((TaskDomain?)null);

        // Act
        var act = () => _sut.EstimateTaskTimeAsync(taskId, CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<ArgumentException>()
            .WithMessage($"Task not found with ID: {taskId}");
    }

    #endregion
}
