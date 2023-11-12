package bg.codeacademy.spring.gossiptalks.gossips;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
public class Gossip {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  @Size(max = 255, message = "Gossip outside of sides bounds!")
  private String text;
  @Pattern(regexp = "^[a-z0-8\\\\.-]+$")
  private String username;
  @NotNull
  private LocalDateTime dateTime;

  public Gossip() {
  }

  public Gossip(String text, String username, LocalDateTime dateTime) {
    this.text = text;
    this.username = username;
    this.dateTime = dateTime;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public LocalDateTime getDateTime() {
    return dateTime;
  }

  public void setDateTime(LocalDateTime dateTime) {
    this.dateTime = dateTime;
  }
}
