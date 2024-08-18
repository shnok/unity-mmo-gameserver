package com.shnok.javaserver.enums.network.packettypes.external;

import java.util.HashMap;
import java.util.Map;

public enum ClientPacketType {
    Ping((byte)0x00),
    ProtocolVersion((byte)0x01),
    AuthLogin((byte)0x02),
    SendMessage((byte)0x03),
    RequestMove((byte)0x04),
    LoadWorld((byte)0x05),
    RequestRotate((byte)0x06),
    RequestAnim((byte)0x07),
    RequestAttack((byte)0x08),
    RequestMoveDirection((byte)0x09),
    RequestSetTarget((byte)0x0A),
    RequestAutoAttack((byte)0x0B),
    CharSelect((byte) 0x0C),
    RequestInventoryOpen((byte) 0x0D),
    RequestInventoryUpdateOrder((byte) 0x0E),
    UseItem((byte) 0x0F),
    RequestUnEquipItem((byte) 0x10),
    RequestDestroyItem((byte) 0x11),
    RequestDropItem((byte) 0x12);

    private final byte value;

    ClientPacketType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    private static final Map<Byte, ClientPacketType> BY_VALUE = new HashMap<>();

    static {
        for (ClientPacketType type : values()) {
            BY_VALUE.put(type.getValue(), type);
        }
    }

    public static ClientPacketType fromByte(byte value) {
        ClientPacketType result = BY_VALUE.get(value);
        if (result == null) {
            throw new IllegalArgumentException("Invalid byte value for ClientPacketType: " + value);
        }
        return result;
    }
}
