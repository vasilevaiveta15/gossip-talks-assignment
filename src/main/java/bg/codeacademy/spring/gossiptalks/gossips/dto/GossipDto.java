package bg.codeacademy.spring.gossiptalks.gossips.dto;

import com.sun.istack.NotNull;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

public class GossipDto
{

  @Size(max = 255)
  @NotEmpty(message = "Cannot create gossip without text.")
  private String text;

  @Pattern(regexp = "[A-Z0-9]+")
  private Long id;

  @Pattern(regexp = "^[a-z0-9.-]+$")
  @NotNull
  private String username;

  private LocalDateTime datetime;


  public String getText()
  {
    return text;
  }

  public void setText(String text)
  {
    this.text = text;
  }

  public Long getId()
  {
    return id;
  }

  public void setId(Long id)
  {
    this.id = id;
  }

  public String getUsername()
  {
    return username;
  }

  public void setUsername(String username)
  {
    this.username = username;
  }

  public LocalDateTime getDatetime()
  {
    return datetime;
  }

  public void setDatetime(LocalDateTime datetime)
  {
    this.datetime = datetime;
  }
}
