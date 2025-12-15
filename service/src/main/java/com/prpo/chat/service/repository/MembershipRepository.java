package com.prpo.chat.service.repository;

import com.prpo.chat.entities.Membership;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipRepository extends MongoRepository<Membership, String> {

}