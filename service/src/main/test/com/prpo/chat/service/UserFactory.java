package com.prpo.chat.service;

import java.util.UUID;

public class UserFactory {

    public static String userId() {
        return UUID.randomUUID().toString();
    }
}
