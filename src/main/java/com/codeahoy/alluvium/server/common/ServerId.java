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
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ServerId serverId1 = (ServerId) o;

        return serverId.equals(serverId1.serverId);

    }

    @Override
    public int hashCode() {
        return serverId.hashCode();
    }

    /**
     * Server Id is a randomly generated unique identifier.
     */
    private final String serverId = UUID.randomUUID().toString();

    @Override
    public String toString() {
        return "ServerId{" +
                "serverId='" + serverId + '\'' +
                '}';
    }

    public String id() {
        return serverId;
    }





}
