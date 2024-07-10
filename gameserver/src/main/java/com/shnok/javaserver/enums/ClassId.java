package com.shnok.javaserver.enums;

public enum ClassId {
    fighter((byte) 0x00, false, Race.human, null, 1),

    warrior((byte) 0x01, false, Race.human, fighter, 20),
    gladiator((byte) 0x02, false, Race.human, warrior, 40),
    warlord((byte) 0x03, false, Race.human, warrior, 40),
    knight((byte) 0x04, false, Race.human, fighter, 20),
    paladin((byte) 0x05, false, Race.human, knight, 40),
    darkAvenger((byte) 0x06, false, Race.human, knight, 40),
    rogue((byte) 0x07, false, Race.human, fighter, 20),
    treasureHunter((byte) 0x08, false, Race.human, rogue, 40),
    hawkeye((byte) 0x09, false, Race.human, rogue, 40),

    mage((byte) 0x0a, true, Race.human, null, 1),
    wizard((byte) 0x0b, true, Race.human, mage, 20),
    sorceror((byte) 0x0c, true, Race.human, wizard, 40),
    necromancer((byte) 0x0d, true, Race.human, wizard, 40),
    warlock((byte) 0x0e, true, Race.human, wizard, 40),
    cleric((byte) 0x0f, true, Race.human, mage, 20),
    bishop((byte) 0x10, true, Race.human, cleric, 40),
    prophet((byte) 0x11, true, Race.human, cleric, 40),

    elvenFighter((byte) 0x12, false, Race.elf, null, 1),
    elvenKnight((byte) 0x13, false, Race.elf, elvenFighter, 20),
    templeKnight((byte) 0x14, false, Race.elf, elvenKnight, 40),
    swordSinger((byte) 0x15, false, Race.elf, elvenKnight, 40),
    elvenScout((byte) 0x16, false, Race.elf, elvenFighter, 20),
    plainsWalker((byte) 0x17, false, Race.elf, elvenScout, 40),
    silverRanger((byte) 0x18, false, Race.elf, elvenScout, 40),

    elvenMage((byte) 0x19, true, Race.elf, null, 1),
    elvenWizard((byte) 0x1a, true, Race.elf, elvenMage, 20),
    spellsinger((byte) 0x1b, true, Race.elf, elvenWizard, 40),
    elementalSummoner((byte) 0x1c, true, Race.elf, elvenWizard, 40),
    oracle((byte) 0x1d, true, Race.elf, elvenMage, 20),
    elder((byte) 0x1e, true, Race.elf, oracle, 40),

    darkFighter((byte) 0x1f, false, Race.darkelf, null, 1),
    palusKnight((byte) 0x20, false, Race.darkelf, darkFighter, 20),
    shillienKnight((byte) 0x21, false, Race.darkelf, palusKnight, 40),
    bladedancer((byte) 0x22, false, Race.darkelf, palusKnight, 40),
    assassin((byte) 0x23, false, Race.darkelf, darkFighter, 20),
    abyssWalker((byte) 0x24, false, Race.darkelf, assassin, 40),
    phantomRanger((byte) 0x25, false, Race.darkelf, assassin, 40),

    darkMage((byte) 0x26, true, Race.darkelf, null, 1),
    darkWizard((byte) 0x27, true, Race.darkelf, darkMage, 20),
    spellhowler((byte) 0x28, true, Race.darkelf, darkWizard, 40),
    phantomSummoner((byte) 0x29, true, Race.darkelf, darkWizard, 40),
    shillienOracle((byte) 0x2a, true, Race.darkelf, darkMage, 20),
    shillenElder((byte) 0x2b, true, Race.darkelf, shillienOracle, 40),

    orcFighter((byte) 0x2c, false, Race.orc, null, 1),
    orcRaider((byte) 0x2d, false, Race.orc, orcFighter, 20),
    destroyer((byte) 0x2e, false, Race.orc, orcRaider, 40),
    orcMonk((byte) 0x2f, false, Race.orc, orcFighter, 20),
    tyrant((byte) 0x30, false, Race.orc, orcMonk, 40),

    orcMage((byte) 0x31, false, Race.orc, null, 1),
    orcShaman((byte) 0x32, false, Race.orc, orcMage, 20),
    overlord((byte) 0x33, false, Race.orc, orcShaman, 40),
    warcryer((byte) 0x34, false, Race.orc, orcShaman, 40),

    dwarvenFighter((byte) 0x35, false, Race.dwarf, null, 1),
    scavenger((byte) 0x36, false, Race.dwarf, dwarvenFighter, 20),
    bountyHunter((byte) 0x37, false, Race.dwarf, scavenger, 40),
    artisan((byte) 0x38, false, Race.dwarf, dwarvenFighter, 20),
    warsmith((byte) 0x39, false, Race.dwarf, artisan, 40);

    // class identifier
    private final byte id;

    // class baselevel
    private final int baseLevel;

    // true is class is mage
    private final boolean isMage;

    // race of the class
    private final Race race;

    // parent ClassId or null if this class is a root
    private final ClassId parent;


    ClassId(byte id, boolean isMage, Race race, ClassId parent, int baseLevel) {
        this.id = id;
        this.isMage = isMage;
        this.race = race;
        this.parent = parent;
        this.baseLevel = baseLevel;
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
    public int getBaseLevel() {
        return baseLevel;
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
