package com.shnok.javaserver.enums;

public enum EntityAnimation {
    Wait((byte) 0),
    Walk((byte) 1),
    Run((byte) 2),
    Die((byte) 3),
    Attack((byte) 4),
    AttackWait((byte) 5);

    private final byte value;

    EntityAnimation(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
