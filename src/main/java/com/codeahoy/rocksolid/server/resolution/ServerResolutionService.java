package com.codeahoy.rocksolid.server.resolution;

import com.codeahoy.rocksolid.server.common.ServerId;
import com.codeahoy.rocksolid.server.common.UserId;

/**
 * ServerResolutionService is useful when running a cluster or a group of Alluvium servers and users can connect with any
 * available server. It keeps an online lookup database of online users and the Allumvium server they are connected
 * with. This allow other applications to find users and send them messages.
 *
 * Created by umermansoor on 2017-03-05.
 */
public interface ServerResolutionService {
    void updateUserRegistration(UserId userId, ServerId serverId);
    LookupResult userLookup(UserId userId);
}
