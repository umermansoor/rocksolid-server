package com.codeahoy.alluvium.server.user;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a user connected to Allumvium server.
 *
 * This class is thread-safe because it is immutable.
 *
 * @author umansoor
 */
public class User {
    private final Channel channel;
    private final long connectionStartTimeMillis;

    /**
     * A custom id can be assigned by the application to each user to make it easy to later reference by this id.
     * E.g. this could be the username.
     * Protected by @this
     */
    private String id;

    private static final Logger logger = LoggerFactory.getLogger(User.class);

    public synchronized void assignId(String s) {
        this.id = s;
    }

    public synchronized String getId() {
        return id;
    }

    public boolean isConnected() {
        return channel.isActive();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User that = (User) o;


        if (connectionStartTimeMillis != that.connectionStartTimeMillis) return false;
        return channel.equals(that.channel);
    }

    @Override
    public int hashCode() {
        int result = channel.hashCode();
        result = 31 * result + (int) (connectionStartTimeMillis ^ (connectionStartTimeMillis >>> 32));
        return result;
    }

    public User(Channel channel) {
        this.channel = channel;
        connectionStartTimeMillis = System.currentTimeMillis();
    }

    public void send(Object response) {
        channel.writeAndFlush(response);
    }

    @Override
    public String toString() {
        return "User{" +
                "channel=" + channel +
                ", connectionStartTimeMillis=" + connectionStartTimeMillis +
                ", id='" + id + '\'' +
                '}';
    }
}