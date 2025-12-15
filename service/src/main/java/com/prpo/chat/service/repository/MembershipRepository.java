package com.prpo.chat.service.repository;

import com.prpo.chat.entities.Membership;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipRepository extends MongoRepository<Membership, String> {
  List<Membership> findByUserId(String userId);
  List<Membership> findByServerId(String serverId);
}