using Asp.Versioning;
using FluentValidation;
using TaskManager.Api.Dtos;
using TaskManager.Api.Validators;
using TaskManager.Domain.Models;

namespace TaskManager.Api.Configurations;

internal static class ConfigureServices
{
    public static void AddCustomApiVersioning(this IServiceCollection services)
    {
        var apiVersioningBuilder = services.AddApiVersioning(options =>
        {
            options.AssumeDefaultVersionWhenUnspecified = true;
            options.DefaultApiVersion = new ApiVersion(1, 0); //v1.0 == v1
            options.ReportApiVersions = true;
        });
        apiVersioningBuilder.AddApiExplorer(options =>
        {
            options.GroupNameFormat = "'v'VVV";
            options.SubstituteApiVersionInUrl = true;
        });
    }

    public static void AddValidators(this IServiceCollection services)
    {
        services.AddScoped<IValidator<CreateTaskDTO>, CreateTaskValidator>();
        services.AddScoped<IValidator<CreateUserDTO>, CreateUserValidator>();
    }
}
