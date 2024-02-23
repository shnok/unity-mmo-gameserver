package com.shnok.javaserver.enums.item;

public enum ItemSlot {
    none((byte) 0),
    head((byte) 1),
    chest((byte) 2),
    legs((byte) 3),
    fullarmor((byte) 4),
    gloves((byte) 5),
    feet((byte) 6),
    lhand((byte) 7),
    rhand((byte) 8),
    lrhand((byte) 9),
    ring((byte) 10),
    rfinger((byte) 11),
    lfinger((byte) 12),
    earring((byte) 13),
    lear((byte) 14),
    rear((byte) 15),
    neck((byte) 16),
    underwear((byte) 17);

    private final byte value;

    ItemSlot(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}

