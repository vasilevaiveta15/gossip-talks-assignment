package bg.codeacademy.spring.gossiptalks.user.dto;

import bg.codeacademy.spring.gossiptalks.validators.oldpasswordvalidator.OldPassword;
import bg.codeacademy.spring.gossiptalks.validators.passwordvalidator.Password;

public class ChangePasswordRequest
{
  @OldPassword
  private String oldPassword;

  @Password
  private String password;

  private String passwordConfirmation;

  public String getOldPassword()
  {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword)
  {
    this.oldPassword = oldPassword;
  }

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
