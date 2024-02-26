package com.shnok.javaserver.enums;

public enum Race {
    human((byte) 0),
    elf((byte) 1),
    darkelf((byte) 2),
    orc((byte) 3),
    dwarf((byte) 4);

    private final byte value;

    Race(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}