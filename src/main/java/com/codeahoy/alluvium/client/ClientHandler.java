package com.codeahoy.alluvium.client;

import com.codeahoy.alluvium.server.frontend.AlluviumPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author umer
 */
public class ClientHandler extends SimpleChannelInboundHandler<AlluviumPacket> {
    private ByteBuf buf;
    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        buf = ctx.alloc().buffer(4); // (1)
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, AlluviumPacket alluviumPacket) throws Exception {
        logger.debug("new message {}", alluviumPacket.getMessage() );
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        // Close the connection when an exception is raised.
        cause.printStackTrace();
        ctx.close();
    }
}
