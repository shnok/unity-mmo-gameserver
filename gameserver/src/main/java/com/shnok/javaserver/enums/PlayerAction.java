package com.shnok.javaserver.enums;

public enum PlayerAction {
    SetTarget((byte) 0x00),
    AutoAttack((byte) 0x01),
    Move((byte) 0x02);

    private final byte value;

    PlayerAction(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
