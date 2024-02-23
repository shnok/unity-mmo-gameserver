package com.shnok.javaserver.enums;

public enum ArmorType {
    none((byte) 0),
    light((byte) 1),
    heavy((byte) 2),
    magic((byte) 3),
    pet((byte) 4);

    private final byte value;

    ArmorType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
