package com.prpo.chat.api.controller;

import com.prpo.chat.entities.Membership;
import com.prpo.chat.entities.Server;
import com.prpo.chat.service.MembershipService;
import com.prpo.chat.service.ServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
@Tag(name = "Membership API", description = "Operations related to server-member relations")
public class MembershipController {

  private final MembershipService membershipService;
  private final ServerService serverService;

  @Operation(
          summary = "Get members of a server",
          description = "Returns the list of userIds that are members of the given serverId."
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
          @ApiResponse(responseCode = "404", description = "Server not found")
  })
  @GetMapping("/users")
  public ResponseEntity<List<String>> getUsers(
      @RequestHeader("Server-Id") String serverId
  ) {
    if(!serverService.serverExists(serverId)) {
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND,
              "Server not found"
      );
    }
    final var res = membershipService.getUsersForServer(serverId);
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @Operation(
          summary = "Get servers of a userId",
          description = "Returns the list of servers that userId is a member of."
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Users retrieved successfully. (Empty list, if there are not members)"),
  })
  @GetMapping("/servers")
  public ResponseEntity<List<Server>> getServers(
      @RequestHeader("User-Id") String userId,
      @Parameter(description = "If provided it filters, based on serverType, otherwise it returns all servers.")
      @RequestHeader(value = "Server-Type", required = false) Server.ServerType serverType
  ) {
    final var res = membershipService.getServersForUser(userId, serverType);
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @Operation(
          summary = "Add a member to a server",
          description = "Adds a member to a server, if they are not already a member."
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "User added successfully"),
          @ApiResponse(responseCode = "400", description = "Cannot add users to server, because it is not a server"),
          @ApiResponse(responseCode = "403", description = "User is banned from the server"),
          @ApiResponse(responseCode = "404", description = "Server not found"),
          @ApiResponse(responseCode = "409", description = "User is already a member of the server")
  })
  @PostMapping
  public ResponseEntity<Void> addMember(
    @RequestHeader("User-Id") String userId,
    @RequestHeader("Server-Id") String serverId
  ) {
      membershipService.addMember(serverId, userId, Membership.Role.MEMBER);
      return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
          summary = "Removes user from a server",
          description = "Removes user from server regardless of its type"
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "User removed successfully"),
          @ApiResponse(responseCode = "403", description = "Caller user does not have a high enough role."),
          @ApiResponse(responseCode = "404", description = "Server not found or users not in server"),
  })
  @DeleteMapping
  public ResponseEntity<Void> removeMember(
          @RequestHeader("Caller-User-Id") String callerUserId,
          @RequestHeader("Target-User-Id") String targetUserId,
          @RequestHeader("Server-Id") String serverId
  ) {
    membershipService.removeMember(serverId, callerUserId, targetUserId, false);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
          summary = "Bans user from server",
          description = "Bans and removes user from server if callerUser has high enough role"
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "User banned successfully"),
          @ApiResponse(responseCode = "403", description = "Caller user does not have a high enough role."),
          @ApiResponse(responseCode = "404", description = "Server not found or users not in server"),
  })
  @DeleteMapping("/ban")
  public ResponseEntity<Void> banMember(
          @RequestHeader("Caller-User-Id") String callerUserId,
          @RequestHeader("Target-User-Id") String targetUserId,
          @RequestHeader("Server-Id") String serverId
  ) {
    membershipService.removeMember(serverId, callerUserId, targetUserId, true);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
          summary = "Changes the role",
          description = "Changes the role of the user on a given server"
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "User removed successfully"),
          @ApiResponse(responseCode = "400", description = "Cannot change roles of users in DMs"),
          @ApiResponse(responseCode = "404", description = "Server not found or user is not a member of the server"),
  })
  @PatchMapping
  public ResponseEntity<Void> changeRole(
      @RequestHeader("Caller-User-Id") String callerUserId,
      @RequestHeader("Target-User-Id") String targetUserId,
      @RequestHeader("Server-Id") String serverId,
      @RequestHeader("Role") Membership.Role role
  ) {
    membershipService.changeRole(serverId, callerUserId, targetUserId, role);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @Operation(
          summary = "Get membership"
  )
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "Membership retrieved successfully"),
          @ApiResponse(responseCode = "404", description = "Membership not found")
  })
  @GetMapping("/{id}")
  public ResponseEntity<Membership> getMembership(
          @PathVariable("id") String userId,
          @RequestHeader("Server-Id") String serverId
  ) {
    final var res = membershipService.getMembership(serverId, userId);
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

}
