package com.codeahoy.server.frontend;

import com.codeahoy.server.frontend.messages.Message;
import com.codeahoy.server.frontend.messages.Time;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;

import java.io.IOException;

/**
 * @author umer
 */
public class AlluviumPacket {
    public final int id;
    public final String tag;
    public final byte [] data;

    private static final ObjectMapper objectMapper = new ObjectMapper(new BsonFactory());
    public final static int TAG_LENGTH_IN_CHARS = 4;
    public final static byte[] EMPTY_DATA = new byte[]{};

    private AlluviumPacket(int id, String tag, byte[] data) {
        this.id = id;
        this.tag = tag;
        if (data == null) {
            this.data = EMPTY_DATA;
        } else {
            this.data = data;
        }
    }

    public Message getMessage() {
        if (tag.equals("time")) {
            try {
                return objectMapper.readValue(data, Time.class);
            } catch (IOException ioe) {
                throw new RuntimeException();
            }
        } else {
            return null;
        }
    }

    public static AlluviumPacket newInstanceForBytes(int id, String type, byte[] data) {
        if (type == null || type.length() != TAG_LENGTH_IN_CHARS) {
            throw new IllegalArgumentException("invalid tag");
        }

        return new AlluviumPacket(id, type, data);
    }

    public static AlluviumPacket newInstanceForMessage(int id, String type, Message message) {
        try {
            return newInstanceForBytes(id, type, objectMapper.writeValueAsBytes(message));
        } catch (JsonProcessingException jpe) {
            throw new RuntimeException();
        }
    }
}
