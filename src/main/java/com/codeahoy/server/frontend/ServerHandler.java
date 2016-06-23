package com.codeahoy.server.frontend;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;

/**
 * @author umer
 */
@Component
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Autowired
    private ThreadPoolExecutorFactoryBean threadPoolExecutorFactoryBean;

    private ExecutorService executorService;

    @PostConstruct
    public void initialize() {
        executorService = threadPoolExecutorFactoryBean.getObject();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        executorService.submit( () -> {
            logger.debug("new message from [{}]", ctx.channel());
            ctx.channel().writeAndFlush(msg);
             ;
            });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("connected [{}]", ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("disconnected [{}]", ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Unexpected error on: " + ctx.channel(), cause);
        ctx.channel().close();
    }
}
