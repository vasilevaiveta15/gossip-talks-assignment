package bg.codeacademy.spring.gossiptalks.user;

import bg.codeacademy.spring.gossiptalks.gossips.Gossip;
import bg.codeacademy.spring.gossiptalks.validators.passwordvalidator.Password;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class User implements Comparable<User>
{
  @Id
  @GeneratedValue
  private Long id;

  private String name;

  @Email
  @Column(unique = true)
  private String email;

  @Column(unique = true)
  @Pattern(regexp = "^[a-z0-9.-]+$")
  private String username;

  @Password
  private String password;

  private Boolean following;

  @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Gossip> gossips = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL)
  private List<User> friends = new ArrayList<>();

  public User()
  {
  }

  public User(String name, String email, String username, String password)
  {
    this.name = name;
    this.email = email;
    this.username = username;
    this.password = password;
  }

  public Long getId()
  {
    return id;
  }

  public User setId(Long id)
  {
    this.id = id;
    return this;
  }

  public String getName()
  {
    return name;
  }

  public User setName(String name)
  {
    this.name = name;
    return this;
  }

  public String getEmail()
  {
    return email;
  }

  public User setEmail(String email)
  {
    this.email = email;
    return this;
  }

  public String getUsername()
  {
    return username;
  }

  public User setUsername(String username)
  {
    this.username = username;
    return this;
  }

  public String getPassword()
  {
    return password;
  }

  public User setPassword(String password)
  {
    this.password = password;
    return this;
  }

  public Boolean getFollowing()
  {
    return following;
  }

  public User setFollowing(Boolean following)
  {
    this.following = following;
    return this;
  }

  public List<Gossip> getGossips()
  {
    return gossips;
  }

  public User setGossips(List<Gossip> gossips)
  {
    this.gossips = gossips;
    return this;
  }

  public List<User> getFriends()
  {
    return friends;
  }

  public User setFriends(List<User> friends)
  {
    this.friends = friends;
    return this;
  }

  @Override
  public int compareTo(User user)
  {
    //maintain reversed order of elements (from most to least gossips)
    return user.getGossipsCount() - this.getGossipsCount();
  }

  private int getGossipsCount()
  {
    return this.gossips.size();
  }

  @Override
  public boolean equals(Object o)
  {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return id.equals(user.id) && email.equals(user.email) && username.equals(user.username);
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(id, email, username);
  }
}
