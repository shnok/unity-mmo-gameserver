package com.shnok.javaserver.enums;

public enum ClassId {
    fighter((byte) 0x00, false, Race.human, null),

    warrior((byte) 0x01, false, Race.human, fighter),
    gladiator((byte) 0x02, false, Race.human, warrior),
    warlord((byte) 0x03, false, Race.human, warrior),
    knight((byte) 0x04, false, Race.human, fighter),
    paladin((byte) 0x05, false, Race.human, knight),
    darkAvenger((byte) 0x06, false, Race.human, knight),
    rogue((byte) 0x07, false, Race.human, fighter),
    treasureHunter((byte) 0x08, false, Race.human, rogue),
    hawkeye((byte) 0x09, false, Race.human, rogue),

    mage((byte) 0x0a, true, Race.human, null),
    wizard((byte) 0x0b, true, Race.human, mage),
    sorceror((byte) 0x0c, true, Race.human, wizard),
    necromancer((byte) 0x0d, true, Race.human, wizard),
    warlock((byte) 0x0e, true, Race.human, wizard),
    cleric((byte) 0x0f, true, Race.human, mage),
    bishop((byte) 0x10, true, Race.human, cleric),
    prophet((byte) 0x11, true, Race.human, cleric),

    elvenFighter((byte) 0x12, false, Race.elf, null),
    elvenKnight((byte) 0x13, false, Race.elf, elvenFighter),
    templeKnight((byte) 0x14, false, Race.elf, elvenKnight),
    swordSinger((byte) 0x15, false, Race.elf, elvenKnight),
    elvenScout((byte) 0x16, false, Race.elf, elvenFighter),
    plainsWalker((byte) 0x17, false, Race.elf, elvenScout),
    silverRanger((byte) 0x18, false, Race.elf, elvenScout),

    elvenMage((byte) 0x19, true, Race.elf, null),
    elvenWizard((byte) 0x1a, true, Race.elf, elvenMage),
    spellsinger((byte) 0x1b, true, Race.elf, elvenWizard),
    elementalSummoner((byte) 0x1c, true, Race.elf, elvenWizard),
    oracle((byte) 0x1d, true, Race.elf, elvenMage),
    elder((byte) 0x1e, true, Race.elf, oracle),

    darkFighter((byte) 0x1f, false, Race.darkelf, null),
    palusKnight((byte) 0x20, false, Race.darkelf, darkFighter),
    shillienKnight((byte) 0x21, false, Race.darkelf, palusKnight),
    bladedancer((byte) 0x22, false, Race.darkelf, palusKnight),
    assassin((byte) 0x23, false, Race.darkelf, darkFighter),
    abyssWalker((byte) 0x24, false, Race.darkelf, assassin),
    phantomRanger((byte) 0x25, false, Race.darkelf, assassin),

    darkMage((byte) 0x26, true, Race.darkelf, null),
    darkWizard((byte) 0x27, true, Race.darkelf, darkMage),
    spellhowler((byte) 0x28, true, Race.darkelf, darkWizard),
    phantomSummoner((byte) 0x29, true, Race.darkelf, darkWizard),
    shillienOracle((byte) 0x2a, true, Race.darkelf, darkMage),
    shillenElder((byte) 0x2b, true, Race.darkelf, shillienOracle),

    orcFighter((byte) 0x2c, false, Race.orc, null),
    orcRaider((byte) 0x2d, false, Race.orc, orcFighter),
    destroyer((byte) 0x2e, false, Race.orc, orcRaider),
    orcMonk((byte) 0x2f, false, Race.orc, orcFighter),
    tyrant((byte) 0x30, false, Race.orc, orcMonk),

    orcMage((byte) 0x31, false, Race.orc, null),
    orcShaman((byte) 0x32, false, Race.orc, orcMage),
    overlord((byte) 0x33, false, Race.orc, orcShaman),
    warcryer((byte) 0x34, false, Race.orc, orcShaman),

    dwarvenFighter((byte) 0x35, false, Race.dwarf, null),
    scavenger((byte) 0x36, false, Race.dwarf, dwarvenFighter),
    bountyHunter((byte) 0x37, false, Race.dwarf, scavenger),
    artisan((byte) 0x38, false, Race.dwarf, dwarvenFighter),
    warsmith((byte) 0x39, false, Race.dwarf, artisan);

    // class identifier
    private final byte id;

    // true is class is mage
    private final boolean isMage;

    // race of the class
    private final Race race;

    // parent ClassId or null if this class is a root
    private final ClassId parent;


    ClassId(byte id, boolean isMage, Race race, ClassId parent) {
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

    public byte getId() {
        return id;
    }

    public boolean isMage() {
        return isMage;
    }

    public static ClassId getById(byte id) {
        for (ClassId classId : values()) {
            if (classId.getId() == id) {
                return classId;
            }
        }
        throw new IllegalArgumentException("No ClassId with id " + id);
    }
}
