package com.shnok.javaserver.enums.item;

public enum EtcItemType {
    none((byte) 0),
    arrow((byte) 1),
    material((byte) 2),
    pet_collar((byte) 3),
    potion((byte) 4),
    recipe((byte) 5),
    scroll((byte) 6),
    quest((byte) 7),
    money((byte) 8),
    other((byte) 9),
    spellbook((byte) 10),
    seed((byte) 11),
    shot((byte) 12),
    herb((byte) 13);

    private final byte value;

    EtcItemType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
