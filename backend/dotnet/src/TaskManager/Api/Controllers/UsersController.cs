using Asp.Versioning;
using Microsoft.AspNetCore.Mvc;
using TaskManager.Api.Dtos;
using TaskManager.Api.Mapper;
using TaskManager.Application.Service;

namespace TaskManager.Api.Controllers;

[ApiVersion("1.0")]
public class UsersController(IUserService userService) : BaseController
{
    private readonly IUserService _userService = userService;

    // GET: api/v1/users
    [HttpGet]
    public async Task<ActionResult<IEnumerable<UserDTO>>> Get(CancellationToken cancellationToken)
    {
        var users = await _userService.GetAllUsersAsync(cancellationToken);
        return Ok(users.ToDto());
    }

    // GET api/v1/users/5
    [HttpGet("{id}")]
    public async Task<ActionResult<UserDTO>> Get(Guid id, CancellationToken cancellationToken)
    {
        var user = await _userService.GetUserByIdAsync(id, cancellationToken);
        if (user is null)
        {
            return NotFound();
        }
        return Ok(user.ToDto());
    }

    // POST api/v1/users
    [HttpPost]
    public async Task<ActionResult<UserDTO>> Post([FromBody] CreateUserDTO createUserDTO, CancellationToken cancellationToken)
    {
        var user = createUserDTO.ToModel();
        var createdUser = await _userService.CreateUserAsync(user, cancellationToken);
        return CreatedAtAction(nameof(Post), new { id = createdUser.Id }, createdUser.ToDto());
    }

    // PUT api/v1/users/5
    [HttpPut("{id}")]
    public async Task<ActionResult<UserDTO>> Put(Guid id, [FromBody] CreateUserDTO createUserDTO, CancellationToken cancellationToken)
    {
        var user = createUserDTO.ToModel();
        var updatedUser = await _userService.UpdateUserAsync(id, user, cancellationToken);
        return Ok(updatedUser.ToDto());
    }

    // DELETE api/v1/users/5
    [HttpDelete("{id}")]
    public async Task<IActionResult> Delete(Guid id, CancellationToken cancellationToken)
    {
        var deleted = await _userService.DeleteUserAsync(id, cancellationToken);
        if (!deleted)
        {
            return NotFound();
        }
        return NoContent();
    }

    /*// GET api/v1/users/5/tasks
    [HttpGet("{id}/users")]
    public async Task<string> GetTask(int id, CancellationToken cancellationToken)
    {
        return "value";
    }*/
}
