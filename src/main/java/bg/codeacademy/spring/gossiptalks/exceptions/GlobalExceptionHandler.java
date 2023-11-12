package bg.codeacademy.spring.gossiptalks.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler
{
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException exception)
  {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, String>> handleConstraintViolationException(ConstraintViolationException exception)
  {
    StringBuilder message = new StringBuilder();
    Set<ConstraintViolation<?>> violations = exception.getConstraintViolations();
    for (ConstraintViolation<?> violation : violations) {
      message.append(violation.getMessage().concat("; "));
    }

    Map<String, String> validationErrors = new HashMap<>();
    validationErrors.put("Error: ", message.toString());

    return new ResponseEntity<>(validationErrors, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(PasswordMismatchException.class)
  public ResponseEntity<String> handlePasswordMismatchException(PasswordMismatchException exception)
  {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AuthenticationServiceException.class)
  public ResponseEntity<String> handleBadCredentialsException(AuthenticationServiceException exception)
  {
    return new ResponseEntity<>(exception.getMessage(), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(InvalidException.class)
  public ResponseEntity<String> handlerInvalidException(InvalidException ex)
  {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
  }
}
