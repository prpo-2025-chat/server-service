package com.prpo.chat.service;

import com.prpo.chat.entities.Membership;
import com.prpo.chat.entities.Server;
import com.prpo.chat.service.repository.MembershipRepository;
import com.prpo.chat.service.repository.ServerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {
    @Autowired
    private final MembershipRepository membershipRepository;
    @Autowired
    private final ServerRepository serverRepository;

    /**
     * Adds a user to the server (of type GROUP) as a member with a specific role
     *
     * @param serverId the ID of the server to which the user will be added
     * @param userId   the ID of the user that will be added to the server
     * @param role     the role the user will be given
     */
    public void addMember(
            final String serverId,
            final String userId,
            final Membership.Role role) {

        if (isMemberOfServer(userId, serverId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    String.format("User %s is already member of the server %s", userId, serverId)
            );
        }

        final var server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Server with id " + serverId + " not found"
                ));

        if (!server.getType().equals(Server.ServerType.GROUP)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Cannot add users to server %s, because it is not a server", userId)
            );
        }

        final var membership = new Membership();
        membership.setServerId(serverId);
        membership.setUserId(userId);
        membership.setRole(role);
        membershipRepository.save(membership);
    }

    /**
     * Removes the user from the server (GROUP or DM)
     *
     * @param serverId
     * @param userId
     */
    public void removeMember(
            final String serverId,
            final String userId) {

        final var server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Server with id " + serverId + " not found"
                ));

        if (!isMemberOfServer(userId, serverId)) {
            return;
        }

        membershipRepository.deleteByServerIdAndUserId(serverId, userId);
    }

    /**
     * Retrieves the list of servers that the given user is a member of,
     * optionally filtering by server type.
     *
     * @param userId     the ID of the user whose servers are being fetched
     * @param serverType the type of servers to filter by (e.g. GROUP, DM);
     *                   if null, all servers regardless of type are returned
     * @return a list of {@link Server} that the user belongs to, filtered by {@link Server.ServerType}
     */
    public List<Server> getServersForUser(final String userId, final Server.ServerType serverType) {
        final var serverIds = membershipRepository.findByUserId(userId).stream()
                .map(Membership::getServerId)
                .toList();

        var servers = serverRepository.findAllById(serverIds);
        if (serverType == null) {
            return servers;
        }

        servers = servers.stream()
                .filter(server -> server.getType() == serverType)
                .toList();
        return servers;
    }

    /**
     * Retrieves the list of users that are members of the given server
     *
     * @param serverId the ID of the server
     * @return a list of userIds that are members of the server
     */
    public List<String> getUsersForServer(final String serverId) {
        return membershipRepository.findByServerId(serverId).stream()
                .map(Membership::getUserId)
                .toList();
    }

    /**
     * Changes the role of user on server
     *
     * @param serverId server must be of type GROUP
     * @param userId
     * @param role
     */
    public void changeRole(
            final String serverId,
            final String userId,
            final Membership.Role role) {

        final var server = serverRepository.findById(serverId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Server with id " + serverId + " not found"
                ));

        if (!server.getType().equals(Server.ServerType.GROUP)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot change roles of users in DMs"
            );
        }

        final var membership = membershipRepository
                .findByServerIdAndUserId(serverId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("User %s is not a member of the server %s", userId, serverId)
                ));

        membership.setRole(role);
        membershipRepository.save(membership);
    }

    private boolean isMemberOfServer(final String userId, final String serverId) {
        return membershipRepository.findByServerIdAndUserId(serverId, userId).isPresent();
    }
}
