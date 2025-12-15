package com.prpo.chat.api.controller;

import com.prpo.chat.entities.Server;
import com.prpo.chat.service.MembershipService;
import com.prpo.chat.service.ServerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/servers")
@RequiredArgsConstructor
public class ServerController {

  @Autowired
  private final ServerService serverService;

  @Autowired
  private final MembershipService membershipService;

  /**
   * Creates a Server and assignes the person who created it as ADMIN
   */
  @PostMapping
  public ResponseEntity<Server> createServer(
      @RequestHeader("User-Id") String userId,
      @Valid @RequestBody Server server
  ) {
    final var res = serverService.createServer(server, userId);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .header("Location", "/api/servers/" + res.getId())
        .body(res);
  }

  /**
   * Creates a DM and assignes both people MEMBER roles
   */
  @PostMapping("/dm")
  public ResponseEntity<Server> createDM(
      @RequestHeader("User1-Id") String firstId,
      @RequestHeader("User2-Id") String secondId,
      @Valid @RequestBody Server server
  ) {
    final var res = serverService.createDM(server, firstId, secondId);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .header("Location", "/api/servers/" + res.getId())
        .body(res);
  }

  @GetMapping("/id")
  public ResponseEntity<Server> getServerById(
      @RequestParam("id") String serverId
  ) {
    final var res = serverService.getServer(serverId);
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  // changePfp
}
