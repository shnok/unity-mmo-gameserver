package com.shnok.javaserver.enums.packettypes;

import java.util.HashMap;
import java.util.Map;

public enum LoginServerPacketType {
    Ping((byte)0),
    InitLS((byte)1),
    Fail((byte)2);

    private final byte value;

    LoginServerPacketType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    private static final Map<Byte, LoginServerPacketType> BY_VALUE = new HashMap<>();

    static {
        for (LoginServerPacketType type : values()) {
            BY_VALUE.put(type.getValue(), type);
        }
    }

    public static LoginServerPacketType fromByte(byte value) {
        LoginServerPacketType result = BY_VALUE.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Invalid byte value for ClientPacketType: " + value);
        }
        return result;
    }
}
