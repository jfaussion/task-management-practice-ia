using FluentValidation;
using TaskManager.Api.Dtos;

namespace TaskManager.Api.Validators;

public class CreateTaskValidator : AbstractValidator<CreateTaskDTO>
{
    public CreateTaskValidator()
    {
        RuleFor(task => task.Title).NotEmpty().WithMessage("Title is required.");
        RuleFor(task => task.Status).NotNull().WithMessage("Status is required.").Must(BeValidStatus).WithMessage("Invalid status.");
        RuleFor(task => task.Priority).Must(BeValidPriority).WithMessage("Invalid priority.");
    }

    private bool BeValidStatus(string status)
    {
        status = status.ToUpper();
        return status == "TODO" || status == "IN_PROGRESS" || status == "DONE";
    }

    private bool BeValidPriority(string priority)
    {
        priority = priority.ToUpper();
        return priority == "LOW" || priority == "MEDIUM" || priority == "HIGH";
    }
}
