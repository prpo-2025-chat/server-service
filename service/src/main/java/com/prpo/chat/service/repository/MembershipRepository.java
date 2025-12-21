package com.prpo.chat.service.repository;

import com.prpo.chat.entities.Membership;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends MongoRepository<Membership, String> {
  List<Membership> findByUserId(String userId);
  List<Membership> findByServerId(String serverId);
  Optional<Membership> findByServerIdAndUserId(String serverId, String userId);
  void deleteByServerIdAndUserId(String serverId, String userId);
  void deleteByServerId(String serverId);
}