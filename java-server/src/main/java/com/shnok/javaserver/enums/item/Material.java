package com.shnok.javaserver.enums;

public enum Material {
    crystal((byte) 0),
    steel((byte) 1),
    fine_steel((byte) 2),
    blood_steel((byte) 3),
    bronze((byte) 4),
    silver((byte) 5),
    gold((byte) 6),
    mithril((byte) 7),
    oriharukon((byte) 8),
    paper((byte) 9),
    wood((byte) 10),
    cloth((byte) 11),
    leather((byte) 12),
    bone((byte) 13),
    horn((byte) 14),
    damascus((byte) 15),
    adamantite((byte) 16),
    chrysolite((byte) 17),
    liquid((byte) 18),
    scale_of_dragon((byte) 19),
    dyestuff((byte) 20),
    cobweb((byte) 21),
    seed((byte) 22);

    private final byte value;

    Material(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}
