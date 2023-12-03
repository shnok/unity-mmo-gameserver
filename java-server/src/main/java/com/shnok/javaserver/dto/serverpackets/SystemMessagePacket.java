package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class SystemMessagePacket extends ServerPacket {

    public MessageType type;

    public SystemMessagePacket(MessageType type) {
        super(ServerPacketType.SystemMessage.getValue());
        this.type = type;
    }

    public SystemMessagePacket(MessageType type, String value) {
        super(ServerPacketType.SystemMessage.getValue());
        this.type = type;

        writeB((byte) type.ordinal());
        writeS(value);

        buildPacket();
    }

    public enum MessageType {
        USER_LOGGED_IN,
        USER_LOGGED_OFF
    }
}
