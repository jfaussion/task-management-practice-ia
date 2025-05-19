using FluentValidation;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using TaskManager.Api.Dtos;
using TaskManager.Api.Mapper;

namespace TaskManager.Api.Configurations;

public class ValidationFilter(ILogger<ValidationFilter> logger, IServiceProvider serviceProvider) : IAsyncActionFilter
{
    private readonly ILogger<ValidationFilter> _logger = logger;
    private readonly IServiceProvider _serviceProvider = serviceProvider;

    public async Task OnActionExecutionAsync(ActionExecutingContext context, ActionExecutionDelegate next)
    {
        foreach (var argument in context.ActionArguments.Values)
        {
            if (argument is null) continue;
            var validatorType = typeof(IValidator<>).MakeGenericType(argument.GetType());
            if (_serviceProvider.GetService(validatorType) is not IValidator validator) continue;
            var validationResult = await validator.ValidateAsync(new ValidationContext<object>(argument));
            if (!validationResult.IsValid)
                validationResult.AddToModelState(context.ModelState);
        }

        if (!context.ModelState.IsValid)
        {
            //var errorsInModelState = context.ModelState.Where(p => p.Value.Errors.Count > 0)
            //    .ToDictionary(keyValuePair => keyValuePair.Key, keyValuePair => keyValuePair.Value.Errors
            //       .Select(p => p.ErrorMessage)).ToArray();
            //var messageErrorsInModelState = string.Join(Environment.NewLine, errorsInModelState);

            var errors = context.ModelState.Values
            .SelectMany(v => v.Errors)
            .Select(e => e.ErrorMessage)
            .Distinct()
            .ToList();

            var messageErrors = errors != null ? string.Join(" ", errors) : "";

            var errorId = Guid.NewGuid();
            _logger.LogError($"Validation error occurred [{errorId}]: {messageErrors}");

            var errorResponse = new ErrorResponse(
                ErrorId: errorId,
                TimeStamp: DateTime.UtcNow,                
                Status: StatusCodes.Status400BadRequest,
                Error: "Validation Error",
                Message: messageErrors,
                Path: context.HttpContext.Request.Path.Value ?? ""
                );

            context.Result = new BadRequestObjectResult(errorResponse);
            return;
        }
        await next();
    }

}
