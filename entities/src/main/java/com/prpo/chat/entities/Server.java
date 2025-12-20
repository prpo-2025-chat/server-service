package com.prpo.chat.entities;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "server")
public class Server {

  @Id
  private String id;

  private ServerType type;

  @NotBlank
  @Size(min = 2, max = 50)
  private String name;

  private Profile profile;

  @Data
  public static class Profile {
    private String avatarUrl = "https://example.com/avatar.jpg";
    private String bio = "";
  }

  public enum ServerType {
    GROUP,
    DM
  }

}
