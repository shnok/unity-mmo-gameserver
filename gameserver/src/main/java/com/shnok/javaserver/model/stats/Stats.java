package com.shnok.javaserver.model.stats;

import java.util.HashMap;
import java.util.Map;

// Source l2j

/**
 * Enum of basic stats.
 * @author mkizub
 */
public enum Stats {
    // Base stats, for each in Calculator a slot is allocated

    // HP, MP & CP
    MAX_HP("maxHp"),
    MAX_MP("maxMp"),
    MAX_CP("maxCp"),
    MAX_RECOVERABLE_HP("maxRecoverableHp"), // The maximum HP that is able to be recovered trough heals
    MAX_RECOVERABLE_MP("maxRecoverableMp"),
    MAX_RECOVERABLE_CP("maxRecoverableCp"),
    REGENERATE_HP_RATE("regHp"),
    REGENERATE_CP_RATE("regCp"),
    REGENERATE_MP_RATE("regMp"),
    MANA_CHARGE("manaCharge"),
    HEAL_EFFECT("healEffect"),

    // ATTACK & DEFENCE
    POWER_DEFENCE("pDef"),
    MAGIC_DEFENCE("mDef"),
    POWER_ATTACK("pAtk"),
    MAGIC_ATTACK("mAtk"),
    PHYSICAL_SKILL_POWER("physicalSkillPower"),
    POWER_ATTACK_SPEED("pAtkSpd"),
    MAGIC_ATTACK_SPEED("mAtkSpd"), // Magic Skill Casting Time Rate
    ATK_REUSE("atkReuse"), // Bows Hits Reuse Rate
    P_REUSE("pReuse"), // Physical Skill Reuse Rate
    MAGIC_REUSE_RATE("mReuse"), // Magic Skill Reuse Rate
    DANCE_REUSE("dReuse"), // Dance Skill Reuse Rate
    SHIELD_DEFENCE("sDef"),

    CRITICAL_DAMAGE("critDmg"),
    CRITICAL_DAMAGE_POS("critDmgPos"),
    CRITICAL_DAMAGE_ADD("critDmgAdd"), // this is another type for special critical damage mods - vicious stance, critical power and critical damage SA
    MAGIC_CRIT_DMG("mCritPower"),

    // PVP BONUS
    PVP_PHYSICAL_DMG("pvpPhysDmg"),
    PVP_MAGICAL_DMG("pvpMagicalDmg"),
    PVP_PHYS_SKILL_DMG("pvpPhysSkillsDmg"),
    PVP_PHYSICAL_DEF("pvpPhysDef"),
    PVP_MAGICAL_DEF("pvpMagicalDef"),
    PVP_PHYS_SKILL_DEF("pvpPhysSkillsDef"),

    // ATTACK & DEFENCE RATES
    EVASION_RATE("rEvas"),
    P_SKILL_EVASION("pSkillEvas"),
    DEFENCE_CRITICAL_RATE("defCritRate"),
    DEFENCE_CRITICAL_RATE_ADD("defCritRateAdd"),
    DEFENCE_CRITICAL_DAMAGE("defCritDamage"),
    DEFENCE_CRITICAL_DAMAGE_ADD("defCritDamageAdd"), // Resistance to critical damage in value (Example: +100 will be 100 more critical damage, NOT 100% more).
    SHIELD_RATE("rShld"),
    CRITICAL_RATE("critRate"),
    CRITICAL_RATE_POS("critRatePos"),
    BLOW_RATE("blowRate"),
    MCRITICAL_RATE("mCritRate"),
    BONUS_EXP("bonusExp"),
    BONUS_SP("bonusSp"),
    ATTACK_CANCEL("cancel"),

    // ACCURACY & RANGE
    ACCURACY_COMBAT("accCombat"),
    POWER_ATTACK_RANGE("pAtkRange"),
    MAGIC_ATTACK_RANGE("mAtkRange"),
    ATTACK_COUNT_MAX("atkCountMax"),
    // Run speed, walk & escape speed are calculated proportionally, magic speed is a buff
    MOVE_SPEED("runSpd"),

    // BASIC STATS
    STAT_STR("STR"),
    STAT_CON("CON"),
    STAT_DEX("DEX"),
    STAT_INT("INT"),
    STAT_WIT("WIT"),
    STAT_MEN("MEN"),

    // Special stats, share one slot in Calculator

    // VARIOUS
    BREATH("breath"),
    FALL("fall"),

    // VULNERABILITIES
    DAMAGE_ZONE_VULN("damageZoneVuln"),
    MOVEMENT_VULN("movementVuln"),
    CANCEL_VULN("cancelVuln"), // Resistance for cancel type skills
    DEBUFF_VULN("debuffVuln"),
    BUFF_VULN("buffVuln"),

    // RESISTANCES
    FIRE_RES("fireRes"),
    WIND_RES("windRes"),
    WATER_RES("waterRes"),
    EARTH_RES("earthRes"),
    HOLY_RES("holyRes"),
    DARK_RES("darkRes"),
    MAGIC_SUCCESS_RES("magicSuccRes"),

    // ELEMENT POWER
    FIRE_POWER("firePower"),
    WATER_POWER("waterPower"),
    WIND_POWER("windPower"),
    EARTH_POWER("earthPower"),
    HOLY_POWER("holyPower"),
    DARK_POWER("darkPower"),

    // PROFICIENCY
    CANCEL_PROF("cancelProf"),

    REFLECT_DAMAGE_PERCENT("reflectDam"),
    REFLECT_SKILL_MAGIC("reflectSkillMagic"),
    REFLECT_SKILL_PHYSIC("reflectSkillPhysic"),
    VENGEANCE_SKILL_MAGIC_DAMAGE("vengeanceMdam"),
    VENGEANCE_SKILL_PHYSICAL_DAMAGE("vengeancePdam"),
    ABSORB_DAMAGE_PERCENT("absorbDam"),
    TRANSFER_DAMAGE_PERCENT("transDam"),
    MANA_SHIELD_PERCENT("manaShield"),
    TRANSFER_DAMAGE_TO_PLAYER("transDamToPlayer"),
    ABSORB_MANA_DAMAGE_PERCENT("absorbDamMana"),

    WEIGHT_LIMIT("weightLimit"),
    WEIGHT_PENALTY("weightPenalty"),
    ENLARGE_ABNORMAL_SLOT("enlargeAbnormalSlot"),

    // ExSkill
    INV_LIM("inventoryLimit"),
    WH_LIM("whLimit"),
    FREIGHT_LIM("freightLimit"),
    P_SELL_LIM("privateSellLimit"),
    P_BUY_LIM("privateBuyLimit"),
    REC_D_LIM("dwarfRecipeLimit"),
    REC_C_LIM("commonRecipeLimit"),

    // C4 Stats
    PHYSICAL_MP_CONSUME_RATE("physicalMpConsumeRate"),
    MAGICAL_MP_CONSUME_RATE("magicalMpConsumeRate"),
    DANCE_MP_CONSUME_RATE("danceMpConsumeRate"),
    BOW_MP_CONSUME_RATE("bowMpConsumeRate"),
    MP_CONSUME("mpConsume"),

    // Shield Stats
    SHIELD_DEFENCE_ANGLE("shieldDefAngle"),

    // Skill mastery
    SKILL_CRITICAL("skillCritical"),
    SKILL_CRITICAL_PROBABILITY("skillCriticalProbability"),

    // Vitality
    VITALITY_CONSUME_RATE("vitalityConsumeRate"),

    // Souls
    MAX_SOULS("maxSouls"),

    REDUCE_EXP_LOST_BY_PVP("reduceExpLostByPvp"),
    REDUCE_EXP_LOST_BY_MOB("reduceExpLostByMob"),
    REDUCE_EXP_LOST_BY_RAID("reduceExpLostByRaid"),

    REDUCE_DEATH_PENALTY_BY_PVP("reduceDeathPenaltyByPvp"),
    REDUCE_DEATH_PENALTY_BY_MOB("reduceDeathPenaltyByMob"),
    REDUCE_DEATH_PENALTY_BY_RAID("reduceDeathPenaltyByRaid"),

    // Fishing
    FISHING_EXPERTISE("fishingExpertise");

    public static final int NUM_STATS = values().length;

    private static final Map<String, Stats> STATS = new HashMap<>();

    private final String name;

    static {
        for (Stats stat : values()) {
            STATS.put(stat.name, stat);
        }
    }

    Stats(String name) {
        this.name = name;
    }

    public static Stats valueOfXml(String name) {
        return STATS.get(name);
    }
}
