using Microsoft.EntityFrameworkCore;
using TaskManager.Domain.Models;

namespace TaskManager.Infrastructure.EFCore;

internal static class TaskManagementContextSeed
{
    public static async Task SeedAsync(this TaskManagementContext context, CancellationToken cancellationToken)
    {
        // Check if the database is accessible
        await context.Database.OpenConnectionAsync(cancellationToken);

        // Seed data here
        if (!await context.Users.AnyAsync(cancellationToken: cancellationToken))
        {
            await context.Users.AddRangeAsync(GetPreconfiguredUsers(), cancellationToken);
        }

        if (!await context.Tasks.AnyAsync(cancellationToken: cancellationToken))
        {
            await context.Tasks.AddRangeAsync(GetPreconfiguredTasks(), cancellationToken);
        }

        await context.SaveChangesAsync(cancellationToken);
    }

    private static IEnumerable<User> GetPreconfiguredUsers()
    {
        return
        [
            new(){
                Id = Guid.Parse("550e8400-e29b-41d4-a716-446655440000"),
                Username = "alice",
                Role = "ADMIN",
                Email = "alice@example.com",
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            },
            new(){
                Id = Guid.Parse("550e8400-e29b-41d4-a716-446655440001"),
                Username = "bob",
                Role = "USER",
                Email = "bob@example.com",
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            },
            new(){
                Id = Guid.Parse("550e8400-e29b-41d4-a716-446655440002"),
                Username = "charlie",
                Role = "USER",
                Email = "charlie@example.com",
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            },
            new(){
                Id = Guid.Parse("550e8400-e29b-41d4-a716-446655440003"),
                Username = "david",
                Role = "USER",
                Email = "david@example.com",
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            },
            new(){
                Id = Guid.Parse("550e8400-e29b-41d4-a716-446655440004"),
                Username = "eve",
                Role = "ADMIN",
                Email = "eve@example.com",
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow
            },
        ];
    }

    static IEnumerable<TaskDomain> GetPreconfiguredTasks()
    {
        return 
        [
            new(){
                Id = Guid.Parse("550e8400-e29b-41d4-a716-446655440010"),
                Title = "Task 1",
                Description = "Description for task 1",
                Status = "TODO",
                Priority = "High",
                DueDate = DateOnly.Parse("2023-12-01"),
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow,
                AssigneeId = Guid.Parse("550e8400-e29b-41d4-a716-446655440000")
            },
            new(){
                Id = Guid.Parse("550e8400-e29b-41d4-a716-446655440011"),
                Title = "Task 2",
                Description = "Description for task 2",
                Status = "IN_PROGRESS",
                Priority = "Medium",
                DueDate = DateOnly.Parse("2023-12-05"),
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow,
                AssigneeId = Guid.Parse("550e8400-e29b-41d4-a716-446655440001")
            },
            new(){
                Id = Guid.Parse("550e8400-e29b-41d4-a716-446655440012"),
                Title = "Task 3",
                Description = "Description for task 3",
                Status = "DONE",
                Priority = "Low",
                DueDate = DateOnly.Parse("2023-12-10"),
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow,
                AssigneeId = Guid.Parse("550e8400-e29b-41d4-a716-446655440002")
            },
            new(){
                Id = Guid.Parse("550e8400-e29b-41d4-a716-446655440013"),
                Title = "Task 4",
                Description = "Description for task 4",
                Status = "TODO",
                Priority = "Medium",
                DueDate = DateOnly.Parse("2023-12-15"),
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow,
                AssigneeId = Guid.Parse("550e8400-e29b-41d4-a716-446655440003")
            },
            new(){
                Id = Guid.Parse("550e8400-e29b-41d4-a716-446655440014"),
                Title = "Task 5",
                Description = "Description for task 5",
                Status = "IN_PROGRESS",
                Priority = "High",
                DueDate = DateOnly.Parse("2023-12-20"),
                CreatedAt = DateTime.UtcNow,
                UpdatedAt = DateTime.UtcNow,
                AssigneeId = Guid.Parse("550e8400-e29b-41d4-a716-446655440004")
            }
        ];
    }
}