package com.prpo.chat.entities;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "user-server")
public class Membership {

  @Id
  private String id;

  @NotBlank
  private String userId;
  @NotBlank
  private String serverId;

  private Role role = Role.MEMBER;

  public enum Role {
    MEMBER,
    ADMIN,
    OWNER
  }
}