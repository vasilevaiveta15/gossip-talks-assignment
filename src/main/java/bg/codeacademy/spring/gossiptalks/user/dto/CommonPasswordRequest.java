package bg.codeacademy.spring.gossiptalks.user.dto;

import bg.codeacademy.spring.gossiptalks.validators.passwordvalidator.Password;
import com.sun.istack.NotNull;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;

public class CommonPasswordRequest
{
  @Password
  private String password;

  private String passwordConfirmation;

  public String getPassword()
  {
    return password;
  }

  public void setPassword(String password)
  {
    this.password = password;
  }

  public String getPasswordConfirmation()
  {
    return passwordConfirmation;
  }

  public void setPasswordConfirmation(String passwordConfirmation)
  {
    this.passwordConfirmation = passwordConfirmation;
  }
}
