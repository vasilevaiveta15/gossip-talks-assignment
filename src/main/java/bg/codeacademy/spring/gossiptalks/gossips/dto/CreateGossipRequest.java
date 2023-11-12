package bg.codeacademy.spring.gossiptalks.gossips.dto;

import com.sun.istack.NotNull;
import org.hibernate.validator.constraints.Length;

public class CreateGossipRequest
{
  @Length(max = 255, message = "Max character length is 255.")
  @NotNull
  private String text;

  public String getText()
  {
    return text;
  }

  public void setText(String text)
  {
    this.text = text;
  }
}
