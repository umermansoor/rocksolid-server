package com.codeahoy.alluvium.client;

import com.codeahoy.alluvium.protocol.AlluviumProtocol;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Alluvium user. For demonstration.
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
                            new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4),
                            new ProtobufDecoder(AlluviumProtocol.Response.getDefaultInstance()),
                            new LengthFieldPrepender(4),
                            new ProtobufEncoder(),
                            clientHandler);
                }
            });

            // Start the user.
            ChannelFuture f = b.connect(host, port).sync();
            logger.debug("connecting to the server {} on port {}.", host, port);

            // Request current time
            AlluviumProtocol.ServerTimeRequest serverTimeRequest =
                    AlluviumProtocol.ServerTimeRequest.newBuilder()
                            .setRequestId(UUID.randomUUID().toString())
                            .build();

            AlluviumProtocol.Request request = AlluviumProtocol.Request.newBuilder()
                    .setServerTime(serverTimeRequest)
                    .setType(AlluviumProtocol.Request.Type.SERVERTIME)
                    .build();

            f.channel().writeAndFlush(request);

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
