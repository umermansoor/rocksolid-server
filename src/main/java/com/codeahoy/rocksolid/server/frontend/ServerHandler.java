package com.codeahoy.rocksolid.server.frontend;

import com.codeahoy.rocksolid.protocol.ProtocolBufferMessages;
import com.codeahoy.rocksolid.server.user.Registry;
import com.codeahoy.rocksolid.server.user.User;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.concurrent.ExecutorService;

/**
 *
 * TODO: Document and rename
 * @author umer
 */
@Component
@ChannelHandler.Sharable
class ServerHandler extends SimpleChannelInboundHandler<ProtocolBufferMessages.Request> {
    @Autowired
    private ThreadPoolExecutorFactoryBean threadPoolExecutorFactoryBean;

    @Autowired
    private Registry registry;

    private ExecutorService executorService;

    private RequestProcessor requestProcessor = new RequestProcessor();

    private static final Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    @PostConstruct
    public void initialize() {
        executorService = threadPoolExecutorFactoryBean.getObject();
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ProtocolBufferMessages.Request request) throws Exception {
        executorService.submit( () -> {
            logger.debug("new message from [{}]", ctx.channel());

            User user = registry.getUserByChannelId(ctx.channel().id()).orElse(null);

            if (user == null) {
                logger.warn("ignoring incoming request [{}] from non-connected channel [{}]",
                        request.getType(), ctx.channel().id());
            } else {
                requestProcessor.process(user, request);
            }
        });
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("connected [{}]", ctx.channel());
        clientConnected(ctx.channel());
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        logger.debug("disconnected [{}]", ctx.channel());
        clientDisconnected(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("Unexpected error on: " + ctx.channel(), cause);
        ctx.channel().close();
    }

    private void clientConnected(Channel channel) {
        assert channel != null;
        User user = new User(channel);
        registry.registerUserByChannel(channel.id(), user);
    }

    private void clientDisconnected(Channel channel) {
        assert channel != null;
        registry.removeByChannelId(channel.id());
    }


    private class RequestProcessor {

        public void process(User user, ProtocolBufferMessages.Request request) {
            if (request.getType().equals(ProtocolBufferMessages.Request.Type.SERVERTIME)) {
                processServerTimeRequest(user, request.getServerTime());
            } else if (request.getType().equals(ProtocolBufferMessages.Request.Type.LOGIN)) {
                processLoginRequest(user, request.getLoginRequest());
            }

        }

        private void processLoginRequest(User user, ProtocolBufferMessages.LoginRequest loginRequest) {
            boolean success = registry.registerUserByIdentity(loginRequest.getId(), user);

            ProtocolBufferMessages.LoginResponse loginResponse;
            if (success) {
                loginResponse = ProtocolBufferMessages.LoginResponse.newBuilder()
                        .setTransactionId(loginRequest.getTransactionId())
                        .setCode(200)
                        .setMessage("success")
                        .build();
            } else { // user registration failed. send invalid response.
                loginResponse = ProtocolBufferMessages.LoginResponse.newBuilder()
                        .setTransactionId(loginRequest.getTransactionId())
                        .setCode(400)
                        .setMessage("failed")
                        .build();
            }

            ProtocolBufferMessages.Response response = ProtocolBufferMessages.Response.newBuilder()
                    .setType(ProtocolBufferMessages.Response.Type.LOGIN)
                    .setLoginResponse(loginResponse)
                    .build();

            user.send(response);
        }

        private void processServerTimeRequest(User user, ProtocolBufferMessages.ServerTimeRequest serverTimeRequest) {
            ProtocolBufferMessages.ServerTimeResponse serverTimeResponse =
                    ProtocolBufferMessages.ServerTimeResponse.newBuilder()
                            .setTransactionId(serverTimeRequest.getTransactionId())
                            .setServerTime(Instant.now().toString())
                            .build();

            ProtocolBufferMessages.Response response = ProtocolBufferMessages.Response.newBuilder()
                    .setType(ProtocolBufferMessages.Response.Type.SERVERTIME)
                    .setServerTime(serverTimeResponse)
                    .build();

            user.send(response);
        }
    }
}
