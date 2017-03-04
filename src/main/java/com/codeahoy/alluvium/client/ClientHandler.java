package com.codeahoy.alluvium.client;

import com.codeahoy.alluvium.protocol.AlluviumProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author umer
 */
public class ClientHandler extends SimpleChannelInboundHandler<AlluviumProtocol.Response> {
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void channelRead0(ChannelHandlerContext ctx, AlluviumProtocol.Response response) throws Exception {
        if (response.getType().equals(AlluviumProtocol.Response.Type.SERVERTIME)) {
            AlluviumProtocol.ServerTimeResponse serverTimeResponse = response.getServerTime();
            logger.debug("new message {} {}", serverTimeResponse.getRequestId(), serverTimeResponse.getServerTime());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
