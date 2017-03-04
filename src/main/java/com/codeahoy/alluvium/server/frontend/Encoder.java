package com.codeahoy.alluvium.server.frontend;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author umer
 */
public class Encoder extends MessageToByteEncoder <AlluviumPacket> {

    private static final Logger logger = LoggerFactory.getLogger(Encoder.class);

    @Override
    public void encode(final ChannelHandlerContext paramChannelHandlerContext, final AlluviumPacket message,
                       final ByteBuf out) throws Exception {
        logger.debug("encoding");
        out.writeIntLE(message.data.length + AlluviumPacket.TAG_LENGTH_IN_CHARS + 4);  /* Length */
        out.writeIntLE(stringAsInt(message.tag));                                      /* Tag */
        out.writeIntLE(message.id);                                                    /* ID */
        out.writeBytes(message.data);                                                  /* Data */
    }


    /**
     * Take an <code>String</code> and returns its <code>int</code> representation.
     *
     * @param
     * @return
     */
    public static int stringAsInt(String in) {
        byte[] b = in.getBytes(Charsets.US_ASCII);

        if (b.length > AlluviumPacket.TAG_LENGTH_IN_CHARS) {
            throw new IllegalArgumentException("The byte array must have at most 4 bytes.");
        }

        int value = 0;
        for (int i = 0; i < b.length; i++) {
            value = (value << 8) | (b[i] & 0xFF);
        }
        return value;
    }

}