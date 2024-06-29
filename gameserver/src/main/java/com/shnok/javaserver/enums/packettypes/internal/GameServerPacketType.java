package com.shnok.javaserver.enums.packettypes.internal;

import java.util.HashMap;
import java.util.Map;

public enum GameServerPacketType {
    BlowFishKey((byte) 0),
    AuthRequest((byte) 1),
    ServerStatus((byte) 2),
    PlayerInGame((byte) 3),
    PlayerLogout((byte) 4),
    ReplyCharacters((byte) 5),
    PlayerAuthRequest((byte) 6);

    private final byte value;

    GameServerPacketType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    private static final Map<Byte, GameServerPacketType> BY_VALUE = new HashMap<>();

    static {
        for (GameServerPacketType type : values()) {
            BY_VALUE.put(type.getValue(), type);
        }
    }

    public static GameServerPacketType fromByte(byte value) {
        GameServerPacketType result = BY_VALUE.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Invalid byte value for ClientPacketType: " + value);
        }
        return result;
    }
}