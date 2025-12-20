package com.prpo.chat.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@Schema(description = "Request DTO to create a new server")
public class ServerCreateRequest {
    @NotBlank
    @Size(min=2,max = 50)
    @Schema(
            description = "Name of the server",
            example = "New server",
            minLength = 2,
            maxLength = 50
    )
    private String name;

    @NotBlank
    private Server.ServerType type;

    private Server.Profile profile;
}
