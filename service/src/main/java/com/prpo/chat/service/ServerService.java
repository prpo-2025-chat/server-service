package com.prpo.chat.service;

import com.prpo.chat.entities.Membership;
import com.prpo.chat.entities.Server;
import com.prpo.chat.service.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServerService {
  @Autowired
  private final ServerRepository serverRepository;
  @Autowired
  private final MembershipService membershipService;

  public Server createServer(final Server server, final String userId) {
    if(server.getProfile() == null) {
      server.setProfile(new Server.Profile());
    }
    server.setType(Server.ServerType.GROUP);

    final var newServer = serverRepository.save(server);
    membershipService.addMember(newServer.getId(), userId, Membership.Role.OWNER);
    return newServer;
  }

  public Server createDM(final Server server, final String firstUserId, final String secondUserId) {
    server.setType(Server.ServerType.DM);

    final var newServer = serverRepository.save(server);
    membershipService.addMember(newServer.getId(), firstUserId, Membership.Role.MEMBER);
    membershipService.addMember(newServer.getId(), secondUserId, Membership.Role.MEMBER);
    return newServer;
  }

  public Server getServer(final String serverId) {
    return serverRepository.findById(serverId).orElse(null);
  }

}
