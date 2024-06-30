package com.shnok.javaserver.enums.packettypes.external;

import java.util.HashMap;
import java.util.Map;

public enum ServerPacketType {
    Ping((byte)0x00),
    Key((byte)0x01),
    CharSelectionInfo((byte)0x02),
    MessagePacket((byte)0x03),
    SystemMessage((byte)0x04),
    PlayerInfo((byte)0x05),
    ObjectPosition((byte)0x06),
    RemoveObject((byte)0x07),
    ObjectRotation((byte)0x08),
    ObjectAnimation((byte)0x09),
    ApplyDamage((byte)0x0A),
    NpcInfo((byte)0x0B),
    ObjectMoveTo((byte)0x0C),
    UserInfo((byte)0x0D),
    ObjectMoveDirection((byte)0x0E),
    GameTimePacket((byte)0x0F),
    EntitySetTarget((byte)0x10),
    AutoAttackStart((byte)0x11),
    AutoAttackStop((byte)0x12),
    ActionFailed((byte)0x13);

    private final byte value;

    ServerPacketType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    private static final Map<Byte, ServerPacketType> BY_VALUE = new HashMap<>();

    static {
        for (ServerPacketType type : values()) {
            BY_VALUE.put(type.getValue(), type);
        }
    }

    public static ServerPacketType fromByte(byte value) {
        ServerPacketType result = BY_VALUE.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Invalid byte value for ClientPacketType: " + value);
        }
        return result;
    }
}
