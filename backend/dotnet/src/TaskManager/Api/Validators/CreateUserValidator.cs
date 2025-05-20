using FluentValidation;
using TaskManager.Api.Dtos;

namespace TaskManager.Api.Validators;

public class CreateUserValidator : AbstractValidator<CreateUserDTO>
{
    public CreateUserValidator()
    {
        RuleFor(user => user.Username)
            .NotEmpty()
            .WithMessage("Username is required.");
        RuleFor(user => user.Email)
            .EmailAddress()
            .WithMessage("Invalid email format.");
        RuleFor(user => user.Role)
            .NotNull()
            .WithMessage("Role is required.")
            .Must(BeValidRole)
            .WithMessage("Invalid role.");
    }

    private bool BeValidRole(string role)
    {
        role = role.ToUpper();
        return role == "ADMIN" || role == "USER";
    }
}
