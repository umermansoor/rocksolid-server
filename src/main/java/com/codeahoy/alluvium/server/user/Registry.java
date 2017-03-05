package com.codeahoy.alluvium.server.user;

import io.netty.channel.ChannelId;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author umansoor
 */
@Component
public class Registry {
    Map<ChannelId, User> channelIdUserMap = new ConcurrentHashMap<>();
    Map<String, User> idUserMap = new ConcurrentHashMap<>();

    public Optional<User> getUserByChannelId(ChannelId channelId) {
         return Optional.ofNullable(channelIdUserMap.get(channelId));
    }

    public Optional<User> getUserById(String id) {
        return Optional.ofNullable(idUserMap.get(id));
    }

    public void addUser(ChannelId channelId, User user) {
        channelIdUserMap.put(channelId, user);
    }

    public synchronized void removeByChannelId(ChannelId channelId) {
        User user = getUserByChannelId(channelId).orElseThrow(IllegalStateException::new);
        if (user.getId() != null) {
            idUserMap.remove(user.getId());
        }

        channelIdUserMap.remove(channelId);




    }

}
