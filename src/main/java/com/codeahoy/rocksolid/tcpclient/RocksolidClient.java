package com.codeahoy.rocksolid.tcpclient;

import com.codeahoy.rocksolid.protocol.ProtocolBufferMessages;
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
 * Alluvium user. For demonstration only.
 *
 * @author umer
 */
public class RocksolidClient {
    private static final Logger logger = LoggerFactory.getLogger(RocksolidClient.class);

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
                            new ProtobufDecoder(ProtocolBufferMessages.Response.getDefaultInstance()),
                            new LengthFieldPrepender(4),
                            new ProtobufEncoder(),
                            clientHandler);
                }
            });

            // Start the user.
            ChannelFuture f = b.connect(host, port).sync();
            logger.debug("connecting to the server {} on port {}.", host, port);

            // Request current time
            f.channel().writeAndFlush(serverTimeRequest());

            // Login
            f.channel().writeAndFlush(loginRequest());


            // Wait for the connection to be closed.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }

    }

    private static ProtocolBufferMessages.Request serverTimeRequest() {
        ProtocolBufferMessages.ServerTimeRequest serverTimeRequest =
                ProtocolBufferMessages.ServerTimeRequest.newBuilder()
                        .setTransactionId(UUID.randomUUID().toString())
                        .build();

        ProtocolBufferMessages.Request request = ProtocolBufferMessages.Request.newBuilder()
                .setServerTime(serverTimeRequest)
                .setType(ProtocolBufferMessages.Request.Type.SERVERTIME)
                .build();

        return request;
    }

    private static ProtocolBufferMessages.Request loginRequest() {
        ProtocolBufferMessages.LoginRequest loginRequest =
                ProtocolBufferMessages.LoginRequest.newBuilder()
                        .setTransactionId(UUID.randomUUID().toString())
                        .setId("1")
                        .build();

        ProtocolBufferMessages.Request request = ProtocolBufferMessages.Request.newBuilder()
                .setLoginRequest(loginRequest)
                .setType(ProtocolBufferMessages.Request.Type.LOGIN)
                .build();

        return request;

    }

    public static void main(String... args) throws Exception{
        connectToHost("localhost", 6677);
    }
}
