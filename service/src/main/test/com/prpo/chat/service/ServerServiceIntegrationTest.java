package com.prpo.chat.service;

import com.prpo.chat.TestApp;
import com.prpo.chat.entities.Membership;
import com.prpo.chat.entities.Server;
import com.prpo.chat.service.repository.ServerRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(classes = TestApp.class)
@ActiveProfiles("test")
class ServerServiceIntegrationTest extends IntegrationTestBase {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    private ServerService serverService;

    @Autowired
    private ServerRepository serverRepository;

    @Autowired
    private MembershipService membershipService;

    @AfterEach
    void cleanDb() {
        mongoTemplate.getDb().drop();
    }

    @Test
    void containerShouldBeRunning() {
        assertThat(AbstractMongoIntegrationTest.mongo.isRunning()).isTrue();
    }

    @Test
    void createGroupServer_createsServerAndOwnerMembership() {
        var creatorId = "user-1";

        var request = ServerCreateRequestFactory.groupServer("My Group");

        var server = serverService.createServer(request, creatorId, null);

        assertThat(server.getId()).isNotNull();
        assertThat(server.getType()).isEqualTo(Server.ServerType.GROUP);

        var membership = membershipService.getMembership(server.getId(), creatorId);
        assertThat(membership.getRole()).isEqualTo(Membership.Role.OWNER);
    }

    @Test
    void createDmServer_createsServerAndTwoMembers() {
        var creatorId = "user-1";
        var otherUserId = "user-2";

        var request = ServerCreateRequestFactory.dmServer("DM");

        var server = serverService.createServer(request, creatorId, otherUserId);

        assertThat(server.getType()).isEqualTo(Server.ServerType.DM);

        assertThat(
                membershipService.getMembership(server.getId(), creatorId).getRole()
        ).isEqualTo(Membership.Role.MEMBER);

        assertThat(
                membershipService.getMembership(server.getId(), otherUserId).getRole()
        ).isEqualTo(Membership.Role.MEMBER);
    }

    @Test
    void createDmServer_withoutUserId_throwsBadRequest() {
        var request = ServerCreateRequestFactory.dmServer("Invalid DM");

        assertThatThrownBy(() ->
                serverService.createServer(request, "creator", null)
        )
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User ID header is required");
    }

    @Test
    void deleteGroupServer_byOwner_deletesServerAndMemberships() {
        var creatorId = "owner";

        var server = serverService.createServer(
                ServerCreateRequestFactory.groupServer("To Delete"),
                creatorId,
                null
        );

        serverService.deleteServer(server.getId(), creatorId);

        assertThat(serverRepository.existsById(server.getId())).isFalse();
        assertThatThrownBy(() ->
                membershipService.getMembership(server.getId(), creatorId)
        ).isInstanceOf(ResponseStatusException.class);
    }

    @Test
    void deleteServer_notGroup_throwsConflict() {
        var creatorId = "user-1";
        var otherUserId = "user-2";

        var server = serverService.createServer(
                ServerCreateRequestFactory.dmServer("DM"),
                creatorId,
                otherUserId
        );

        assertThatThrownBy(() ->
                serverService.deleteServer(server.getId(), creatorId)
        )
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Server is not a GROUP");
    }

    @Test
    void deleteGroupServer_notOwner_throwsForbidden() {
        var ownerId = "owner";
        var memberId = "member";

        var server = serverService.createServer(
                ServerCreateRequestFactory.groupServer("Group"),
                ownerId,
                null
        );

        membershipService.addMember(server.getId(), memberId, Membership.Role.MEMBER);

        assertThatThrownBy(() ->
                serverService.deleteServer(server.getId(), memberId)
        )
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("User is not the owner");
    }
}
