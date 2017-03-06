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
    private UserId userId;

    private static final Logger logger = LoggerFactory.getLogger(User.class);

    /**
     *
     * @param channel
     * @throws NullPointerException if channel is null.
     */
    public User(Channel channel) {
        if (channel == null) {
            throw new NullPointerException("channel cannot be null.");
        }

        this.channel = channel;
        connectionStartTimeMillis = System.currentTimeMillis();
    }

    public boolean isOnline() {
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

    public void send(Object response) {
        channel.writeAndFlush(response);
    }

    @Override
    public String toString() {
        return "User{" +
                "channel=" + channel +
                ", connectionStartTimeMillis=" + connectionStartTimeMillis +
                ", id='" + userId + '\'' +
                '}';
    }

    synchronized void assignId(UserId u) {
        this.userId = u;
    }

    public synchronized UserId id() {
        return userId;
    }

}
