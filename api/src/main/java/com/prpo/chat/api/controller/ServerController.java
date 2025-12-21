package com.prpo.chat.api.controller;

import com.prpo.chat.entities.Server;
import com.prpo.chat.entities.ServerCreateRequest;
import com.prpo.chat.service.ServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
@Tag(name = "Server API", description = "Operations related to servers")
public class ServerController {

    private final ServerService serverService;

    //TODO: Profile can be partially complete. The other thing should be set to default
    @Operation(
            summary = "Create server",
            description = "Create a server of type GROUP or DM. " +
                    "Assign creator as OWNER for GROUP, and both users as MEMBERS for DM. " +
                    "User-Id header is required only for DM servers."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Server created"),
            @ApiResponse(responseCode = "400", description = "Missing User-Id header for DM server")
    })
    @PostMapping
    public ResponseEntity<Server> createServer(
            @RequestHeader("Creator-Id") String creatorId,
            @Parameter(description = "Required only when creating a DM server")
            @RequestHeader(value = "User-Id", required = false) String userId,
            @Valid @RequestBody ServerCreateRequest serverRequest
    ) {
        final var server = serverService.createServer(serverRequest, creatorId, userId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header("Location", "/api/servers/" + server.getId())
                .body(server);
    }

    @Operation(
            summary = "Get server by id"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Server found"),
            @ApiResponse(responseCode = "404", description = "Server not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Server> getServerById(
            @PathVariable("id") String serverId
    ) {
        final var server = serverService.getServer(serverId);
        return new ResponseEntity<>(server, HttpStatus.OK);
    }

    @Operation(
            summary = "Delete server",
            description = "Deletes server, if it's of type GROUP and callerUser has role OWNER"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Server successfully deleted"),
            @ApiResponse(responseCode = "403", description = "User is not the owner"),
            @ApiResponse(responseCode = "404", description = "Server not found"),
            @ApiResponse(responseCode = "409", description = "Server is not a GROUP")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteServer(
            @PathVariable("id") String serverId,
            @RequestHeader("Caller-Id") String callerUserId
    ) {
        serverService.deleteServer(serverId, callerUserId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // changePfp
}
