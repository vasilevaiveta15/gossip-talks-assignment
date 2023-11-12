package bg.codeacademy.spring.gossiptalks.exceptions;

public class PasswordMismatchException extends RuntimeException
{
  public PasswordMismatchException(String message)
  {
    super(message);
  }
}
