package com.prpo.chat.service;

import com.prpo.chat.entities.Membership;
import com.prpo.chat.service.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {
  @Autowired
  private final MembershipRepository membershipRepository;

  public void addMember(
      final String serverId,
      final String userId,
      final Membership.Role role) {

    final var membership = new Membership();
    membership.setServerId(serverId);
    membership.setUserId(userId);
    membership.setRole(role);
    membershipRepository.save(membership);
  }

  public List<String> getServersForUser(final String userId) {
    return membershipRepository.findByUserId(userId).stream()
        .map(Membership::getServerId)
        .toList();
  }

  public List<String> getUsersForServer(final String serverId) {
    return membershipRepository.findByServerId(serverId).stream()
        .map(Membership::getUserId)
        .toList();
  }

  public void changeRole(
      final String serverId,
      final String userId,
      final Membership.Role role) {
    // TODO: figure out how to write this nicer
    membershipRepository.findByUserId(userId).stream()
        .filter(membership -> membership.getServerId().equals(serverId))
        .forEach(membership -> {
          membership.setRole(role);
          membershipRepository.save(membership);
        });
  }

  public void removeMember(
      final String serverId,
      final String userId) {
    membershipRepository.deleteByServerIdAndUserId(serverId, userId);
  }

}
