package com.shnok.javaserver.enums.network.packettypes.external;

import java.util.HashMap;
import java.util.Map;

public enum ServerPacketType {
    Ping((byte)0x00),
    Key((byte)0x01),
    LoginFail((byte)0x02),
    CharSelectionInfo((byte)0x03),
    MessagePacket((byte)0x04),
    SystemMessage((byte)0x05),
    PlayerInfo((byte)0x06),
    ObjectPosition((byte)0x07),
    RemoveObject((byte)0x08),
    ObjectRotation((byte)0x09),
    ObjectAnimation((byte)0x0A),
    ApplyDamage((byte)0x0B),
    NpcInfo((byte)0x0C),
    ObjectMoveTo((byte)0x0D),
    UserInfo((byte)0x0E),
    ObjectMoveDirection((byte)0x0F),
    GameTimePacket((byte)0x10),
    EntitySetTarget((byte)0x11),
    AutoAttackStart((byte)0x12),
    AutoAttackStop((byte)0x13),
    ActionFailed((byte)0x14),
    ServerClose((byte)0x15),
    StatusUpdate((byte)0x16),
    ActionAllowed((byte)0x17),
    InventoryItemList((byte)0x18),
    InventoryUpdate((byte)0x19),
    LeaveWorld((byte)0x1A),
    RestartResponse((byte)0x1B);

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
