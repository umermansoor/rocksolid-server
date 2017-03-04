package com.codeahoy.alluvium.server.frontend;

import com.codeahoy.alluvium.protocol.AlluviumProtocol;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.concurrent.ExecutorService;

/**
 * @author umer
 */
@Component
@ChannelHandler.Sharable
public class ServerHandler extends SimpleChannelInboundHandler<AlluviumProtocol.Request> {
    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @Value("${name}")
    public String name;

    @Autowired
    private ThreadPoolExecutorFactoryBean threadPoolExecutorFactoryBean;

    private ExecutorService executorService;

    @PostConstruct
    public void initialize() {
        executorService = threadPoolExecutorFactoryBean.getObject();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, AlluviumProtocol.Request request)
            throws Exception {
        executorService.submit( () -> {
            logger.debug("new message from [{}]", ctx.channel());

            if (request.getType().equals(AlluviumProtocol.Request.Type.SERVERTIME)) {

                AlluviumProtocol.ServerTimeRequest serverTimeRequest = request.getServerTime();

                AlluviumProtocol.ServerTimeResponse serverTimeResponse =
                        AlluviumProtocol.ServerTimeResponse.newBuilder()
                        .setRequestId(serverTimeRequest.getRequestId())
                        .setServerTime(Instant.now().toString())
                        .build();

                AlluviumProtocol.Response response = AlluviumProtocol.Response.newBuilder()
                        .setType(AlluviumProtocol.Response.Type.SERVERTIME)
                        .setServerTime(serverTimeResponse)
                        .build();

                ctx.channel().writeAndFlush(response);
            }
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
