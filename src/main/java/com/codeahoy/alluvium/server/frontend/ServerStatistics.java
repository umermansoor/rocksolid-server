package com.codeahoy.alluvium.server.frontend;

import com.codeahoy.alluvium.server.user.Registry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ScheduledExecutorFactoryBean;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * TODO: temporary until Grafana
 * Created by umermansoor on 2017-03-05.
 */
@Component
class ServerStatistics {


    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    @Autowired
    private Registry registry;

    private static final Logger logger = LoggerFactory.getLogger(ServerStatistics.class);


    ServerStatistics() {
        scheduledExecutorService.scheduleAtFixedRate( () -> {
            try {
                logger.info("There are [{}] currently online. [{}] are logged in.",
                        registry.totalUsers(), registry.totalIdentifiedUsers());
            } catch (Exception exception) {
                logger.warn("{} - an error occurred", Thread.currentThread().getName(), exception);
            }

        }, 5000L, 5000L, TimeUnit.MILLISECONDS);

    }

}
