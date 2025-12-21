package com.prpo.chat.entities;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
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

  private Status status;

  @RequiredArgsConstructor
  public enum Role {
    MEMBER(1),
    ADMIN(2),
    OWNER(3);

    public final int level;

    public boolean canManage(final Role other) {
      return this.level > other.level;
    }
  }

  public enum Status {
    NORMAL,
    BANNED
  }
}