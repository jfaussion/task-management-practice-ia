namespace TaskManager.Domain.Exceptions;

public class FunctionalException : Exception
{
    public FunctionalException(string message) : base(message)
    {
    }
    public FunctionalException(string message, Exception innerException) : base(message, innerException)
    {
    }
}
