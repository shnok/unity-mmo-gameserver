package com.shnok.serverpackets;

public class SystemMessagePacket extends ServerPacket {

    public enum MessageType {
        USER_LOGGED_IN,
        USER_LOGGED_OFF
    }

    public MessageType _type;

    public SystemMessagePacket(MessageType type) {
        super((byte)0x03);
        _type = type;
    }

    public SystemMessagePacket(MessageType type, String value) {
        super((byte)0x03);
        _type = type;

        byte[] valueBytes = value.getBytes();
        byte[] data = new byte[valueBytes.length + 2];

        data[0] = (byte)type.ordinal();
        data[1] = (byte)valueBytes.length;
        System.arraycopy(valueBytes, 0, data, 2, valueBytes.length);

        buildPacket(data);
    }
}
