package com.prpo.chat.service;

import com.prpo.chat.entities.Membership;
import com.prpo.chat.service.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
