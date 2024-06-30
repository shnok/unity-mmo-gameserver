package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;

public class SystemMessagePacket extends SendablePacket {

    public final MessageType type;

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
