package com.codeahoy.client;

import com.codeahoy.server.frontend.AlluviumPacket;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import com.codeahoy.server.frontend.Decoder;
import com.codeahoy.server.frontend.Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteOrder;

/**
 * Alluvium client. For demonstration.
 *
 * @author umer
 */
public class AlluviumClient {
    private static final Logger logger = LoggerFactory.getLogger(AlluviumClient.class);

    public static void connectToHost(String host, int port) throws Exception {
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        final ClientHandler clientHandler = new ClientHandler();
        try {
            Bootstrap b = new Bootstrap();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(
                            new LoggingHandler(LogLevel.DEBUG),
                            new LengthFieldBasedFrameDecoder(
                                    ByteOrder.LITTLE_ENDIAN,
                                    Integer.MAX_VALUE, /* Max Frame Length */
                                    0,                 /* Length field starts at 0th byte */
                                    4,                 /* Length field in an Integer, so 4 bytes */
                                    0,                 /* no adjustment */
                                    4,                 /* Strip out the length field */
                                    false),
                            new Decoder(),
                            new Encoder(),
                            clientHandler);
                }
            });

            // Start the client.
            ChannelFuture f = b.connect(host, port).sync();
            logger.debug("connecting to the server {} on port {}.", host, port);

            // Request current time
            AlluviumPacket alluviumPacket =  AlluviumPacket.newInstanceForBytes(1, "time", null);
            f.channel().writeAndFlush(alluviumPacket).sync();

            // Wait for the connection to be closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String... args) throws Exception{
        connectToHost("localhost", 6677);
    }
}
