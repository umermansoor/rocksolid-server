package com.codeahoy.alluvium.server.frontend;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Arrays;
import java.util.List;

/**
 * @author umer
 */
public final class Decoder extends ByteToMessageDecoder {

    private static final Logger logger = LoggerFactory.getLogger(Decoder.class);

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int type = in.readIntLE();
        int id = in.readIntLE();
        byte[] bytes = new byte[in.readableBytes()];
        in.readBytes(bytes);

        AlluviumPacket message = AlluviumPacket.newInstanceForBytes(id, intToString(type), bytes);
        out.add(message);
        // in.release(); See http://netty.io/4.0/api/io/netty/handler/codec/ByteToMessageDecoder.html
        // Might cause a memory leak
        //ReferenceCountUtil.release(in);
    }

    /**
     * Take an <code>int</code> and returns its <code>String</code> representation.
     *
     * @param a
     * @return
     */
    public static String intToString(int a) {
        byte[] b = new byte[AlluviumPacket.TAG_LENGTH_IN_CHARS];
        int count = 0;

        for (int i = 3; i > -1; i--) {
            byte bt = (byte) ((a >> (24 - (i * 8)) & 0xFF));

            if (bt > 0) {
                b[i] = bt;
                count++;
            } else {
                break;
            }
        }

        return new String(
                count == b.length ? b : Arrays.copyOfRange(b, b.length - count, b.length),
                Charsets.US_ASCII
        );
    }
}
