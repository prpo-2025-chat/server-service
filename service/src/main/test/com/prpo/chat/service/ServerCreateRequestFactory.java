package com.prpo.chat.service;

import com.prpo.chat.entities.Server;
import com.prpo.chat.entities.ServerCreateRequest;

public class ServerCreateRequestFactory {

    public static ServerCreateRequest groupServer(String name) {
        var req = new ServerCreateRequest();
        req.setName(name);
        req.setType(Server.ServerType.GROUP);
        return req;
    }

    public static ServerCreateRequest dmServer(String name) {
        var req = new ServerCreateRequest();
        req.setName(name);
        req.setType(Server.ServerType.DM);
        return req;
    }
}
