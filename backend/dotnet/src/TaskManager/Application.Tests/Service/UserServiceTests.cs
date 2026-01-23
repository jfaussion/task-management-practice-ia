using FluentAssertions;
using Moq;
using TaskManager.Application.Interface;
using TaskManager.Application.Service;
using TaskManager.Domain.Models;

namespace TaskManager.Application.Tests.Service;

public class UserServiceTests
{
    private readonly Mock<IUserRepository> _userRepositoryMock;
    private readonly UserService _sut;

    public UserServiceTests()
    {
        _userRepositoryMock = new Mock<IUserRepository>();
        _sut = new UserService(_userRepositoryMock.Object);
    }

    #region GetAllUsersAsync

    [Fact]
    public async Task GetAllUsersAsync_ShouldReturnAllUsers()
    {
        // Arrange
        var users = new List<User>
        {
            new() { Id = Guid.NewGuid(), Username = "user1" },
            new() { Id = Guid.NewGuid(), Username = "user2" }
        };
        _userRepositoryMock.Setup(r => r.FindAllAsync(It.IsAny<CancellationToken>()))
            .ReturnsAsync(users);

        // Act
        var result = await _sut.GetAllUsersAsync(CancellationToken.None);

        // Assert
        result.Should().BeEquivalentTo(users);
        _userRepositoryMock.Verify(r => r.FindAllAsync(It.IsAny<CancellationToken>()), Times.Once);
    }

    [Fact]
    public async Task GetAllUsersAsync_WhenNoUsers_ShouldReturnEmptyList()
    {
        // Arrange
        _userRepositoryMock.Setup(r => r.FindAllAsync(It.IsAny<CancellationToken>()))
            .ReturnsAsync(new List<User>());

        // Act
        var result = await _sut.GetAllUsersAsync(CancellationToken.None);

        // Assert
        result.Should().BeEmpty();
    }

    #endregion

    #region GetUserByIdAsync

    [Fact]
    public async Task GetUserByIdAsync_WhenUserExists_ShouldReturnUser()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var user = new User { Id = userId, Username = "testuser" };
        _userRepositoryMock.Setup(r => r.FindByIdAsync(userId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(user);

        // Act
        var result = await _sut.GetUserByIdAsync(userId, CancellationToken.None);

        // Assert
        result.Should().BeEquivalentTo(user);
    }

    [Fact]
    public async Task GetUserByIdAsync_WhenUserNotFound_ShouldReturnNull()
    {
        // Arrange
        var userId = Guid.NewGuid();
        _userRepositoryMock.Setup(r => r.FindByIdAsync(userId, It.IsAny<CancellationToken>()))
            .ReturnsAsync((User?)null);

        // Act
        var result = await _sut.GetUserByIdAsync(userId, CancellationToken.None);

        // Assert
        result.Should().BeNull();
    }

    #endregion

    #region GetUserByUsernameAsync

    [Fact]
    public async Task GetUserByUsernameAsync_WhenUserExists_ShouldReturnUser()
    {
        // Arrange
        var user = new User { Id = Guid.NewGuid(), Username = "testuser" };
        _userRepositoryMock.Setup(r => r.FindByUsernameAsync("testuser", It.IsAny<CancellationToken>()))
            .ReturnsAsync(user);

        // Act
        var result = await _sut.GetUserByUsernameAsync("testuser", CancellationToken.None);

        // Assert
        result.Should().BeEquivalentTo(user);
    }

    [Fact]
    public async Task GetUserByUsernameAsync_WhenUserNotFound_ShouldReturnNull()
    {
        // Arrange
        _userRepositoryMock.Setup(r => r.FindByUsernameAsync("unknown", It.IsAny<CancellationToken>()))
            .ReturnsAsync((User?)null);

        // Act
        var result = await _sut.GetUserByUsernameAsync("unknown", CancellationToken.None);

        // Assert
        result.Should().BeNull();
    }

    #endregion

    #region CreateUserAsync

    [Fact]
    public async Task CreateUserAsync_WithUniqueUsername_ShouldResetIdAndSave()
    {
        // Arrange
        var originalId = Guid.NewGuid();
        var user = new User { Id = originalId, Username = "newuser", Email = "new@test.com", Role = "user" };
        User? capturedUser = null;

        _userRepositoryMock.Setup(r => r.ExistsByUsernameAsync("newuser", It.IsAny<CancellationToken>()))
            .ReturnsAsync(false);
        _userRepositoryMock.Setup(r => r.SaveAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()))
            .Callback<User, CancellationToken>((u, _) => capturedUser = u)
            .ReturnsAsync((User u, CancellationToken _) => u);

        // Act
        await _sut.CreateUserAsync(user, CancellationToken.None);

        // Assert
        capturedUser.Should().NotBeNull();
        capturedUser!.Id.Should().BeNull("ID should be reset to null before saving");
        capturedUser.Username.Should().Be("newuser");
        capturedUser.Email.Should().Be("new@test.com");
        capturedUser.Role.Should().Be("user");
        _userRepositoryMock.Verify(r => r.ExistsByUsernameAsync("newuser", It.IsAny<CancellationToken>()), Times.Once);
        _userRepositoryMock.Verify(r => r.SaveAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()), Times.Once);
    }

    [Fact]
    public async Task CreateUserAsync_WithDuplicateUsername_ShouldThrowInvalidOperationException()
    {
        // Arrange
        var user = new User { Username = "existinguser" };
        _userRepositoryMock.Setup(r => r.ExistsByUsernameAsync("existinguser", It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);

        // Act
        var act = () => _sut.CreateUserAsync(user, CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<InvalidOperationException>()
            .WithMessage("A user with this username already exists");
        _userRepositoryMock.Verify(r => r.SaveAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()), Times.Never);
    }

    [Fact]
    public async Task CreateUserAsync_WithNullUsername_ShouldSkipUniquenessCheckAndSave()
    {
        // Arrange
        var user = new User { Id = Guid.NewGuid(), Email = "test@test.com" };
        User? capturedUser = null;

        _userRepositoryMock.Setup(r => r.SaveAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()))
            .Callback<User, CancellationToken>((u, _) => capturedUser = u)
            .ReturnsAsync((User u, CancellationToken _) => u);

        // Act
        await _sut.CreateUserAsync(user, CancellationToken.None);

        // Assert
        capturedUser.Should().NotBeNull();
        capturedUser!.Id.Should().BeNull();
        capturedUser.Email.Should().Be("test@test.com");
        _userRepositoryMock.Verify(r => r.ExistsByUsernameAsync(It.IsAny<string>(), It.IsAny<CancellationToken>()), Times.Never);
        _userRepositoryMock.Verify(r => r.SaveAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()), Times.Once);
    }

    #endregion

    #region UpdateUserAsync

    [Fact]
    public async Task UpdateUserAsync_WhenUserExists_ShouldUpdateProperties()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var originalUser = new User { Id = userId, Username = "original", Email = "original@test.com", Role = "user" };
        var updateUser = new User { Username = "updated", Email = "updated@test.com", Role = "admin" };
        User? capturedUser = null;

        _userRepositoryMock.Setup(r => r.ExistsByIdAsync(userId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);
        _userRepositoryMock.Setup(r => r.FindByIdAsync(userId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(originalUser);
        _userRepositoryMock.Setup(r => r.ExistsByUsernameAsync("updated", It.IsAny<CancellationToken>()))
            .ReturnsAsync(false);
        _userRepositoryMock.Setup(r => r.UpdateAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()))
            .Callback<User, CancellationToken>((u, _) => capturedUser = u)
            .ReturnsAsync((User u, CancellationToken _) => u);

        // Act
        await _sut.UpdateUserAsync(userId, updateUser, CancellationToken.None);

        // Assert
        capturedUser.Should().NotBeNull();
        capturedUser!.Id.Should().Be(userId, "original user ID should be preserved");
        capturedUser.Username.Should().Be("updated");
        capturedUser.Email.Should().Be("updated@test.com");
        capturedUser.Role.Should().Be("admin");
        _userRepositoryMock.Verify(r => r.UpdateAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()), Times.Once);
    }

    [Fact]
    public async Task UpdateUserAsync_WhenUserNotFound_ShouldThrowInvalidOperationException()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var user = new User { Username = "update" };
        _userRepositoryMock.Setup(r => r.ExistsByIdAsync(userId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(false);

        // Act
        var act = () => _sut.UpdateUserAsync(userId, user, CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<InvalidOperationException>()
            .WithMessage($"User not found with ID: {userId}");
        _userRepositoryMock.Verify(r => r.UpdateAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()), Times.Never);
    }

    [Fact]
    public async Task UpdateUserAsync_WithDuplicateUsername_ShouldThrowInvalidOperationException()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var originalUser = new User { Id = userId, Username = "original" };
        var updateUser = new User { Username = "taken" };

        _userRepositoryMock.Setup(r => r.ExistsByIdAsync(userId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);
        _userRepositoryMock.Setup(r => r.FindByIdAsync(userId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(originalUser);
        _userRepositoryMock.Setup(r => r.ExistsByUsernameAsync("taken", It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);

        // Act
        var act = () => _sut.UpdateUserAsync(userId, updateUser, CancellationToken.None);

        // Assert
        await act.Should().ThrowAsync<InvalidOperationException>()
            .WithMessage("A user with this username already exist");
        _userRepositoryMock.Verify(r => r.UpdateAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()), Times.Never);
    }

    [Fact]
    public async Task UpdateUserAsync_WithSameUsername_ShouldSkipUniquenessCheckAndUpdate()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var originalUser = new User { Id = userId, Username = "same", Email = "old@test.com" };
        var updateUser = new User { Username = "same", Email = "new@test.com" };
        User? capturedUser = null;

        _userRepositoryMock.Setup(r => r.ExistsByIdAsync(userId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);
        _userRepositoryMock.Setup(r => r.FindByIdAsync(userId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(originalUser);
        _userRepositoryMock.Setup(r => r.UpdateAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()))
            .Callback<User, CancellationToken>((u, _) => capturedUser = u)
            .ReturnsAsync((User u, CancellationToken _) => u);

        // Act
        await _sut.UpdateUserAsync(userId, updateUser, CancellationToken.None);

        // Assert
        capturedUser.Should().NotBeNull();
        capturedUser!.Email.Should().Be("new@test.com");
        _userRepositoryMock.Verify(r => r.ExistsByUsernameAsync(It.IsAny<string>(), It.IsAny<CancellationToken>()), Times.Never);
        _userRepositoryMock.Verify(r => r.UpdateAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()), Times.Once);
    }

    #endregion

    #region DeleteUserAsync

    [Fact]
    public async Task DeleteUserAsync_WhenUserExists_ShouldDeleteCorrectUserAndReturnTrue()
    {
        // Arrange
        var userId = Guid.NewGuid();
        var user = new User { Id = userId, Username = "todelete" };
        User? capturedUser = null;

        _userRepositoryMock.Setup(r => r.FindByIdAsync(userId, It.IsAny<CancellationToken>()))
            .ReturnsAsync(user);
        _userRepositoryMock.Setup(r => r.DeleteByIdAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()))
            .Callback<User, CancellationToken>((u, _) => capturedUser = u);

        // Act
        var result = await _sut.DeleteUserAsync(userId, CancellationToken.None);

        // Assert
        result.Should().BeTrue();
        capturedUser.Should().NotBeNull();
        capturedUser!.Id.Should().Be(userId);
        capturedUser.Username.Should().Be("todelete");
        _userRepositoryMock.Verify(r => r.DeleteByIdAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()), Times.Once);
    }

    [Fact]
    public async Task DeleteUserAsync_WhenUserNotFound_ShouldReturnFalseWithoutDeleting()
    {
        // Arrange
        var userId = Guid.NewGuid();
        _userRepositoryMock.Setup(r => r.FindByIdAsync(userId, It.IsAny<CancellationToken>()))
            .ReturnsAsync((User?)null);

        // Act
        var result = await _sut.DeleteUserAsync(userId, CancellationToken.None);

        // Assert
        result.Should().BeFalse();
        _userRepositoryMock.Verify(r => r.DeleteByIdAsync(It.IsAny<User>(), It.IsAny<CancellationToken>()), Times.Never);
    }

    #endregion

    #region ExistsByUsernameAsync

    [Fact]
    public async Task ExistsByUsernameAsync_WhenUsernameExists_ShouldReturnTrue()
    {
        // Arrange
        _userRepositoryMock.Setup(r => r.ExistsByUsernameAsync("existing", It.IsAny<CancellationToken>()))
            .ReturnsAsync(true);

        // Act
        var result = await _sut.ExistsByUsernameAsync("existing", CancellationToken.None);

        // Assert
        result.Should().BeTrue();
    }

    [Fact]
    public async Task ExistsByUsernameAsync_WhenUsernameNotExists_ShouldReturnFalse()
    {
        // Arrange
        _userRepositoryMock.Setup(r => r.ExistsByUsernameAsync("nonexisting", It.IsAny<CancellationToken>()))
            .ReturnsAsync(false);

        // Act
        var result = await _sut.ExistsByUsernameAsync("nonexisting", CancellationToken.None);

        // Assert
        result.Should().BeFalse();
    }

    #endregion
}
