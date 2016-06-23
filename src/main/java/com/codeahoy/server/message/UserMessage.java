package com.codeahoy.server.message;

/**
 * @author umer
 */
public class UserMessage {
    public final int id;
    public final String type;
    public final byte [] data;

    public final static int TYPE_LENGTH_IN_CHARS = 4;

    private UserMessage(int id, String type, byte[] data) {
        this.id = id;
        this.type = type;
        this.data = data;
    }

    public static UserMessage newInstance(int id, String type, byte[] data) {
        if (type == null || type.length() != TYPE_LENGTH_IN_CHARS) {
            throw new IllegalArgumentException("invalid type");
        }

        return new UserMessage(id, type, data);
    }
}
