namespace TaskManager.Domain.Models;

public class TaskDomain
{
    public Guid? Id { get; set; }
    public string? Title { get; set; }
    public string? Description  { get; set; }
    public string? Status  { get; set; }
    public string? Priority  { get; set; }
    public Guid? AssigneeId  { get; set; }
    public User? Assignee  { get; set; }
    public DateOnly? DueDate  { get; set; }
    public DateTime? CreatedAt  { get; set; }
    public DateTime? UpdatedAt  { get; set; }
}
