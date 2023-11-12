package bg.codeacademy.spring.gossiptalks.user.dto;

import bg.codeacademy.spring.gossiptalks.validators.passwordvalidator.Password;
import com.sun.istack.NotNull;

import javax.persistence.Column;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

public class CreateUserRequest
{
  @Email(message = "Please use a valid email address")
  private String email;

  @Pattern(regexp ="^[a-z0-9.-]+$", message = "Username must contain digits, lower-case letter, dash and dot!")
  @NotEmpty(message = "Cannot create user without username")
  @Column(unique = true)
  private String username;

  //@NotEmpty(message = "Cannot create a user account without a name")
  @NotNull
  private String name;

  private Boolean following;

  @Password
  private String password;

  private String passwordConfirmation;

  public String getEmail()
  {
    return email;
  }

  public void setEmail(String email)
  {
    this.email = email;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public Boolean getFollowing()
  {
    return following;
  }

  public void setFollowing(Boolean following)
  {
    this.following = following;
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
