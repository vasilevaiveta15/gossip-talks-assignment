package bg.codeacademy.spring.gossiptalks.user.dto;

import javax.validation.constraints.Pattern;

public class UserResponse
{
  private String email;

  @Pattern(regexp = "^[a-z0-9.-]+$")
  private String username;

  private String name;
  private Boolean following;

  public UserResponse(){

  }

  public String getEmail()
  {
    return email;
  }

  public UserResponse setEmail(String email)
  {
    this.email = email;
    return this;
  }

  public String getUsername()
  {
    return username;
  }

  public UserResponse setUsername(String username)
  {
    this.username = username;
    return this;
  }

  public String getName()
  {
    return name;
  }

  public UserResponse setName(String name)
  {
    this.name = name;
    return this;
  }

  public Boolean getFollowing()
  {
    return following;
  }

  public UserResponse setFollowing(Boolean following)
  {
    this.following = following;
    return this;
  }
}
