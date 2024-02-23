package com.shnok.javaserver.enums.item;

public enum WeaponType {
    none((byte) 0),
    sword((byte) 1),
    blunt((byte) 2),
    dagger((byte) 3),
    bow((byte) 4),
    pole((byte) 5),
    etc((byte) 6),
    fist((byte) 7),
    dual((byte) 8),
    dualfist((byte) 9),
    bigsword((byte) 10),
    pet((byte) 11),
    rod((byte) 12),
    bigblunt((byte) 13);

    private final byte value;

    WeaponType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }
}

