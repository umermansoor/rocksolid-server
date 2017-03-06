package com.codeahoy.alluvium.server.common;

import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Each Alluvium server has a unique id so we can uniquely identify a server if we are running a cluser or group of
 * Alluvium servers.
 *
 * For example, suppose you wish to send a message to a user. You can check if the user is online using the Presence
 * Service and if so, identify which Alluvium server the user is connected to so you can send it the message.
 *
 * Created by umermansoor on 2017-03-05.
 */
@Component
public class ServerId {
    /**
     * Server Id is a randomly generated unique identifier.
     */
    private final String serverId = UUID.randomUUID().toString();

    public String id() {
        return serverId;
    }





}
