package com.shnok.javaserver.enums.packettypes.external;

import java.util.HashMap;
import java.util.Map;

public enum ServerPacketType {
    Ping((byte)0),
    AuthResponse((byte)1),
    MessagePacket((byte)2),
    SystemMessage((byte)3),
    PlayerInfo((byte)4),
    ObjectPosition((byte)5),
    RemoveObject((byte)6),
    ObjectRotation((byte)7),
    ObjectAnimation((byte)8),
    ApplyDamage((byte)9),
    NpcInfo((byte)0x0A),
    ObjectMoveTo((byte)0x0B),
    UserInfo((byte)0x0C),
    ObjectMoveDirection((byte)0x0D),
    GameTimePacket((byte)0x0E),
    EntitySetTarget((byte)0x0F),
    AutoAttackStart((byte)0x10),
    AutoAttackStop((byte)0x11),
    ActionFailed((byte)0x12);

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
