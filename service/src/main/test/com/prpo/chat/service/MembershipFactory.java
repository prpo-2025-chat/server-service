package com.prpo.chat.service;

import com.prpo.chat.entities.Membership;

public class MembershipFactory {

    public static Membership owner(String serverId, String userId) {
        var m = new Membership();
        m.setServerId(serverId);
        m.setUserId(userId);
        m.setRole(Membership.Role.OWNER);
        return m;
    }

    public static Membership member(String serverId, String userId) {
        var m = new Membership();
        m.setServerId(serverId);
        m.setUserId(userId);
        m.setRole(Membership.Role.MEMBER);
        return m;
    }
}

