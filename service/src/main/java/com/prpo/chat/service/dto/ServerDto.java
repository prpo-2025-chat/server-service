package com.prpo.chat.service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ServerDto {
  @NotBlank
  private String name;
}
