package com.codeahoy.rocksolid.server.frontend;

import com.codeahoy.rocksolid.server.user.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

/**
 * TODO: temporary until Grafana
 * Created by umermansoor on 2017-03-05.
 */

@Component
class ServerStatistics {
    @Autowired
    private ThreadPoolTaskScheduler taskScheduler;

    @Autowired
    private Registry registry;

    private static final Logger logger = LoggerFactory.getLogger(ServerStatistics.class);

    @PostConstruct
    private void startTask() {
        taskScheduler.scheduleAtFixedRate( () -> {
            logger.info("There are [{}] currently online. [{}] are logged in.",
                    registry.totalUsers(), registry.totalIdentifiedUsers());
        }, 5000L);
    }

}
