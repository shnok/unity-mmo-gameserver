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
    rfinger((byte) 10),
    lfinger((byte) 11),
    lear((byte) 12),
    rear((byte) 13),
    neck((byte) 14),
    underwear((byte) 15),
    ring((byte) 16),
    earring((byte) 17);

    private final byte value;

    ItemSlot(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static ItemSlot getSlot(int value) {
        for (ItemSlot slot : ItemSlot.values()) {
            if (slot.getValue() == value) {
                return slot;
            }
        }
        return null;
    }
}

