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

  /**
   * Deletes server if server has type GROUP and caller is the owner of the server.
   * It deletes all memberships associated with the server as well.
   *
   * @param serverId ID of the server to be deleted
   *                 has to have type GROUP
   * @param callerUserId ID of the person trying to delete the server
   *                     has to be the OWNER of the server.
   */
  public void deleteServer(
          final String serverId,
          final String callerUserId
  ) {
    final var server = getServer(serverId);
    if(server.getType() != Server.ServerType.GROUP) {
      throw new ResponseStatusException(
              HttpStatus.CONFLICT,
              "Server is not a GROUP"
      );
    }

    final var membership = membershipService.getMembership(serverId, callerUserId);
    if(membership.getRole() != Membership.Role.OWNER) {
      throw new ResponseStatusException(
              HttpStatus.FORBIDDEN,
              "User is not the owner."
      );
    }

    membershipService.deleteAllMemberships(serverId);
    serverRepository.deleteById(serverId);

  }

  public Server getServer(final String serverId) {
    return serverRepository
            .findById(serverId).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Server not found"
            ));
  }

  public boolean serverExists(final String serverId) { return serverRepository.existsById(serverId); }

}
