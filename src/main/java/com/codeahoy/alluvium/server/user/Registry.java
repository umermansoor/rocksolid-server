package com.codeahoy.alluvium.server.user;

import com.codeahoy.alluvium.server.common.UserId;
import io.netty.channel.ChannelId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author umansoor
 */
@Component
public class Registry {
    /**
     * A map for keeping {@link ChannelId} to {@link User} mapping.
     */
    private final Map<ChannelId, User> channelIdUserMap = new ConcurrentHashMap<>();

    /**
     * A map for keeping {@link ChannelId} to {@link User} mapping.
     */
    private final Map<UserId, User> idUserMap = new ConcurrentHashMap<>();

    private static final Logger logger = LoggerFactory.getLogger(Registry.class);

    public Optional<User> getUserByChannelId(ChannelId channelId) {
         return Optional.ofNullable(channelIdUserMap.get(channelId));
    }

    public int totalUsers() {
        return channelIdUserMap.size();
    }

    public int totalIdentifiedUsers() {
        return idUserMap.size();
    }

    public Optional<User> getUserById(UserId userId) {
        return Optional.ofNullable(idUserMap.get(userId));
    }

    public Optional<User> getUserById(String userId) {
        return Optional.ofNullable(idUserMap.get(UserId.newInstance(userId)));
    }

    public void registerUserByChannel(ChannelId channelId, User user) {
        channelIdUserMap.put(channelId, user);
    }

    /**
     * This removes users from both
     * @param channelId
     */
    public synchronized void removeByChannelId(ChannelId channelId) {
        User user = getUserByChannelId(channelId).orElseThrow(IllegalStateException::new);
        if (user.id() != null) {
            idUserMap.remove(user.id());
        }

        channelIdUserMap.remove(channelId);
    }

    public synchronized boolean registerUserByIdentity(String userIdentity, User user) {
        User existingUser = getUserById(userIdentity).orElse(null);

        if (existingUser != null && existingUser.isOnline()) { // Ensure that the identity is not already assigned.
            logger.error("unable to assign user id [{}] because the user is online", userIdentity);
            return false;
        } else {
            try {
                UserId userId = UserId.newInstance(userIdentity);
                user.assignId(userId);
                idUserMap.put(userId, user);
                return true;
            } catch (IllegalArgumentException iaex) {
                logger.error("unable to assign user identity. reason: [{}]", iaex.toString());
                return false;
            }
        }
    }
}
