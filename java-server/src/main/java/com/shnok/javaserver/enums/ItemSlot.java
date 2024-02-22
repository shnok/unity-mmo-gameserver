package com.shnok.javaserver.enums;

public enum ItemSlot {
    Head((byte) 0),
    UpperBody((byte) 1),
    Legs((byte) 2),
    Gloves((byte) 3),
    Boots((byte) 4),
    LHand((byte) 5),
    RHand((byte) 6),
    LRing((byte) 7),
    RRing((byte) 8),
    LEar((byte) 9),
    REar((byte) 10),
    Neck((byte) 11);

    private final byte value;

    ItemSlot(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}

