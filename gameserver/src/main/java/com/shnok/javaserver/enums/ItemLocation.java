package com.shnok.javaserver.enums;

public enum ItemLocation {
    VOID((byte) 0),
    EQUIPPED((byte) 1),
    INVENTORY((byte) 2),
    WAREHOUSE((byte) 3);

    private final byte value;

    ItemLocation(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
