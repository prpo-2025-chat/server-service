package com.prpo.chat.service;

import com.prpo.chat.entities.Membership;
import com.prpo.chat.entities.Server;
import com.prpo.chat.entities.ServerCreateRequest;
import com.prpo.chat.service.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ServerService {
  @Autowired
  private final ServerRepository serverRepository;
  @Autowired
  private final MembershipService membershipService;

  /**
   * Creates new server and adds necessary members to it
   */
  public Server createServer(
          final ServerCreateRequest serverRequest,
          final String creatorId,
          final String userId) {
    var server = new Server();
    server.setName(serverRequest.getName());

    if(serverRequest.getProfile() == null) {
      server.setProfile(new Server.Profile());
    } else {
      server.setProfile(serverRequest.getProfile());
    }
    final var serverType = serverRequest.getType();
    server.setType(serverType);
    server = serverRepository.save(server);

    /*
     * If server is GROUP it adds the creator to it as an OWNER
     * and if it is DM it adds both the creator and user to it as MEMBERS
     */
    if(serverType == Server.ServerType.GROUP) {
      membershipService.addMember(server.getId(), creatorId, Membership.Role.OWNER);
    } else if(serverType == Server.ServerType.DM) {
      if(userId == null) {
        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "User ID header is required when creating a DM server"
        );
      }
      membershipService.addMember(server.getId(), creatorId, Membership.Role.MEMBER);
      membershipService.addMember(server.getId(), userId, Membership.Role.MEMBER);
    }

    return server;
  }

  public Server getServer(final String serverId) {
    return serverRepository.findById(serverId).orElse(null);
  }

  public boolean serverExists(final String serverId) { return serverRepository.existsById(serverId); }

}
