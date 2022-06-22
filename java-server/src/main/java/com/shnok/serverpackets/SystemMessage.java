package com.shnok.serverpackets;

public class SystemMessage extends ServerPacket {

    public MessageType _type;

    public SystemMessage(MessageType type) {
        super((byte) 0x03);
        _type = type;
    }

    public SystemMessage(MessageType type, String value) {
        super((byte) 0x03);
        _type = type;

        writeB((byte) type.ordinal());
        writeS(value);

        buildPacket();
    }

    public enum MessageType {
        USER_LOGGED_IN,
        USER_LOGGED_OFF
    }
}
