package com.prpo.chat.api.controller;

import com.prpo.chat.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memberships")
@RequiredArgsConstructor
public class MembershipController {

  @Autowired
  private final MembershipService membershipService;

  @GetMapping("/users")
  public ResponseEntity<List<String>> getUsers(
      @RequestHeader("Server-Id") String serverId
  ) {
    final var res = membershipService.getUsersForServer(serverId);
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @GetMapping("/servers")
  public ResponseEntity<List<String>> getServers(
      @RequestHeader("User-Id") String userId
  ) {
    final var res = membershipService.getServersForUser(userId);
    return new ResponseEntity<>(res, HttpStatus.OK);
  }
}
