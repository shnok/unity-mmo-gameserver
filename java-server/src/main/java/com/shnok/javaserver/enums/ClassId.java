package com.shnok.javaserver.enums;

public enum ClassId {
    fighter((byte) 0x00, false, Race.human, null),
    mage((byte) 0x0a, true, Race.human, null),
    elvenFighter((byte) 0x12, false, Race.elf, null),
    elvenMage((byte) 0x19, true, Race.elf, null),
    darkFighter((byte) 0x1f, false, Race.darkelf, null),
    darkMage((byte) 0x26, true, Race.darkelf, null),
    orcFighter((byte) 0x2c, false, Race.orc, null),
    orcMage((byte) 0x31, false, Race.orc, null),
    dwarvenFighter((byte) 0x35, false, Race.dwarf, null);

    // class identifier
    private final byte id;

    // true is class is mage
    private final boolean isMage;

    // race of the class
    private final Race race;

    // parent ClassId or null if this class is a root
    private final ClassId parent;


    private ClassId(byte id, boolean isMage, Race race, ClassId parent)
    {
        this.id = id;
        this.isMage = isMage;
        this.race = race;
        this.parent = parent;
    }

    // check if class is a child of given clas id
    public final boolean childOf(ClassId id) {
        if (parent == null) {
            return false;
        }

        if (parent == id) {
            return true;
        }

        return parent.childOf(id);
    }

    // check if class id is class or child of id
    public final boolean equalsOrChildOf(ClassId id) {
        return (this == id) || childOf(id);
    }

    // return class depth
    public final int level() {
        if (parent == null) {
            return 0;
        }

        return 1 + parent.level();
    }
}
