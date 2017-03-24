package com.codeahoy.rocksolid.tcpclient;

import com.codeahoy.rocksolid.protocol.ProtocolBufferMessages;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author umer
 */
public class ClientHandler extends SimpleChannelInboundHandler<ProtocolBufferMessages.Response> {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, ProtocolBufferMessages.Response response) throws Exception {
        if (response.getType().equals(ProtocolBufferMessages.Response.Type.SERVERTIME)) {
            ProtocolBufferMessages.ServerTimeResponse serverTimeResponse = response.getServerTime();
            logger.debug(
                    "{} - id: [{}] time: [{}]",
                    serverTimeResponse.getClass().getSimpleName(),
                    serverTimeResponse.getTransactionId(),
                    serverTimeResponse.getServerTime()
            );
        } else if (response.getType().equals(ProtocolBufferMessages.Response.Type.LOGIN)) {
            ProtocolBufferMessages.LoginResponse loginResponse = response.getLoginResponse();
            logger.debug(
                    "{} - id: [{}] code: [{}]",
                    loginResponse.getClass().getSimpleName(),
                    loginResponse.getTransactionId(),
                    loginResponse.getCode()
            );
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
