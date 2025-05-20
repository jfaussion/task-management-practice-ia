using Microsoft.EntityFrameworkCore;
using TaskManager.Domain.Models;

namespace TaskManager.Infrastructure.EFCore;

internal class TaskManagementContext(DbContextOptions<TaskManagementContext> options) : DbContext(options)
{
    public DbSet<User> Users { get; set; }
    public DbSet<TaskDomain> Tasks { get; set; }

    protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
    {
        optionsBuilder.UseAsyncSeeding(async (context, _, cancellationToken) =>
         {
             await this.SeedAsync(cancellationToken);
         });
    }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        modelBuilder.Entity<User>(builder =>
        {
            builder.ToTable("Users");
            builder.HasKey(u => u.Id);
            builder.Property(u => u.Username).IsRequired();
            builder.Property(u => u.Email);
            builder.Property(u => u.Role).IsRequired();
            builder.Property(u => u.CreatedAt).IsRequired().HasDefaultValueSql("CURRENT_TIMESTAMP");
            builder.Property(u => u.UpdatedAt).IsRequired();

            builder.HasIndex(u => u.Username).IsUnique().HasDatabaseName("User_username_key");
        });

        modelBuilder.Entity<TaskDomain>(builder =>
        {
            builder.ToTable("Tasks");
            builder.HasKey(t => t.Id);            
            builder.Property(t => t.Title).IsRequired();
            builder.Property(t => t.Description);
            builder.Property(t => t.Status).IsRequired();
            builder.Property(t => t.Priority);
            builder.Property(t => t.DueDate);
            builder.Property(u => u.CreatedAt).IsRequired().HasDefaultValueSql("CURRENT_TIMESTAMP");
            builder.Property(u => u.UpdatedAt).IsRequired();          
            // A Task has one Assignee (User), which is optional (nullable FK)
            builder.HasOne(d => d.Assignee)
                  // A User can have many AssignedTasks but none defined
                  .WithMany()
                  // The foreign key in the Task table is AssigneeId
                  .HasForeignKey(d => d.AssigneeId)
                  // Specify the constraint name (matches your SQL)
                  .HasConstraintName("Task_assigneeId_fkey")
                  // Behavior when the referenced User is deleted: Set AssigneeId to NULL
                  .OnDelete(DeleteBehavior.SetNull);

            builder.HasIndex(u => new { u.Title, u.AssigneeId }).IsUnique().HasDatabaseName("Task_title_assigneeId_key");
        });
    }
}
