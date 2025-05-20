using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using TaskManager.Application.Interface;
using TaskManager.Infrastructure.EFCore;
using TaskManager.Infrastructure.Repository;

namespace TaskManager.Infrastructure.Configuration;

public static class ConfigureServices
{
    /// <summary>
    /// Configures the infrastructure services for the application.
    /// </summary>
    /// <param name="services"></param>
    /// <param name="configuration"></param>
    /// <returns></returns>
    public static IServiceCollection AddInfrastructureServices(this IServiceCollection services, IConfiguration configuration)
    {
        // Configure DbContext
        // Ensure you have a connection string in your appsettings.json or other configuration source
        // e.g., "DefaultConnection": "Server=(localdb)\\mssqllocaldb;Database=YourAppDb;Trusted_Connection=True;"

        //SQLite
        services.AddDbContext<TaskManagementContext>(options => options.UseSqlite(configuration.GetConnectionString("DefaultSqliteConnection")));

        //SQL Server
        /*services.AddDbContext<TaskManagementContext>(options =>
            options.UseSqlServer(configuration.GetConnectionString("DefaultSqlServerConnection"),
                builder => builder.MigrationsAssembly(typeof(TaskManagementContext).Assembly.FullName));*/

        // Register Repositories
        services.AddScoped<ITaskRepository, TaskRepository>();
        services.AddScoped<IUserRepository, UserRepository>();

        return services;
    }

    /// <summary>
    /// Ensures that the database is created. This method should be called at application startup.
    /// </summary>
    /// <param name="serviceProvider"></param>
    /// <returns></returns>
    public static async Task EnsureDatabaseMigrated(this IServiceProvider serviceProvider)
    {
        await using var scope = serviceProvider.CreateAsyncScope();
        await using var context = scope.ServiceProvider.GetRequiredService<TaskManagementContext>();
        //await context.Database.EnsureCreatedAsync();
        await context.Database.MigrateAsync();
    }
}
