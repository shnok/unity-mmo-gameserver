package com.shnok.javaserver.enums.item;

public enum ItemCategory {
    //00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
    weapon((byte) 0),
    shield_armor((byte) 1),
    jewel((byte) 2),
    quest_item((byte) 3),
    adena((byte) 4),
    item((byte) 5);

    private final byte value;

    ItemCategory(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
