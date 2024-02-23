package com.shnok.javaserver.enums;

public enum Grade {
    none((byte) 0),
    d((byte) 1),
    c((byte) 2),
    b((byte) 3),
    a((byte) 4),
    s((byte) 5);

    private final byte value;

    Grade(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
