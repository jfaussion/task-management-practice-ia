using Microsoft.Extensions.DependencyInjection;
using TaskManager.Application.Service;

namespace TaskManager.Application.Configuration;

public static class ConfigureServices
{
    public static IServiceCollection AddApplicationServices(this IServiceCollection services)
    {
        // Application Services
        services.AddScoped<IUserService, UserService>();
        services.AddScoped<ITaskService, TaskService>();
        return services;
    }
}
