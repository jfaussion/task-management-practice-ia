using Microsoft.AspNetCore.Mvc;
using TaskManager.Api.Configurations;
using TaskManager.Api.Exceptions;
using TaskManager.Application.Configuration;
using TaskManager.Infrastructure.Configuration;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.

// Add FluentValidations
builder.Services.AddValidators();

builder.Services.AddControllers(options =>
{
    options.Filters.Add<ValidationFilter>();
});
builder.Services.Configure<ApiBehaviorOptions>(options => options.SuppressModelStateInvalidFilter = true);

// Learn more about configuring Swagger/OpenAPI at https://aka.ms/aspnetcore/swashbuckle
builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen();

// Add Exception Handling Middleware
builder.Services.AddExceptionHandler<GlobalExceptionHandler>();
builder.Services.AddProblemDetails();

// Add FluentValidations
builder.Services.AddValidators();

builder.Services.AddApplicationServices();
builder.Services.AddInfrastructureServices(builder.Configuration);
builder.Services.AddCustomApiVersioning();

var app = builder.Build();

// Add the exception handler middleware
app.UseExceptionHandler();

// Configure the HTTP request pipeline.
if (app.Environment.IsDevelopment())
{
    await app.Services.EnsureDatabaseMigrated();
    app.UseSwagger();
    app.UseSwaggerUI();
}

else
{
    // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
    app.UseHsts();
}

app.UseHttpsRedirection();

app.MapControllers();

app.Run();
