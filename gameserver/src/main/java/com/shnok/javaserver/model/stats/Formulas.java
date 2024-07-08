package com.shnok.javaserver.model.stats;

import com.shnok.javaserver.db.entity.DBItem;
import com.shnok.javaserver.db.entity.DBWeapon;
import com.shnok.javaserver.dto.external.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.enums.item.WeaponType;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.functions.fomulas.*;
import com.shnok.javaserver.security.Rnd;
import lombok.extern.log4j.Log4j2;

import java.util.Arrays;

import static com.shnok.javaserver.config.Configuration.*;

/**
 * Global calculations.
 */

@Log4j2
public final class Formulas {

    /** Regeneration Task period. */
    private static final int HP_REGENERATE_PERIOD = 3000; // 3 secs

    public static final byte SHIELD_DEFENSE_FAILED = 0; // no shield defense
    public static final byte SHIELD_DEFENSE_SUCCEED = 1; // normal shield defense
    public static final byte SHIELD_DEFENSE_PERFECT_BLOCK = 2; // perfect block

    private static final byte MELEE_ATTACK_RANGE = 40;

    /**
     * Return the period between 2 regeneration task (3s for Entity, 5 min for L2DoorInstance).
     * @param cha
     * @return
     */
    public static int getRegeneratePeriod(Entity cha) {
        return HP_REGENERATE_PERIOD;
    }

    /**
     * Return the standard NPC Calculator set containing ACCURACY_COMBAT and EVASION_RATE.<br>
     * <B><U>Concept</U>:</B><br>
     * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...). In fact, each calculator is a table of Func object in which each Func represents a mathematic function : <br>
     * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<br>
     * To reduce cache memory use, L2NPCInstances who don't have skills share the same Calculator set called <B>NPC_STD_CALCULATOR</B>.<br>
     * @return
     */
    public static Calculator[] getStdNPCCalculators() {
        Calculator[] std = new Calculator[Stats.NUM_STATS];

        std[Stats.MAX_HP.ordinal()] = new Calculator();
        std[Stats.MAX_HP.ordinal()].addFunc(FuncMaxHpMul.getInstance());

        std[Stats.MAX_MP.ordinal()] = new Calculator();
        std[Stats.MAX_MP.ordinal()].addFunc(FuncMaxMpMul.getInstance());

        std[Stats.POWER_ATTACK.ordinal()] = new Calculator();
        std[Stats.POWER_ATTACK.ordinal()].addFunc(FuncPAtkMod.getInstance());

        std[Stats.MAGIC_ATTACK.ordinal()] = new Calculator();
        std[Stats.MAGIC_ATTACK.ordinal()].addFunc(FuncMAtkMod.getInstance());

        std[Stats.POWER_DEFENCE.ordinal()] = new Calculator();
        std[Stats.POWER_DEFENCE.ordinal()].addFunc(FuncPDefMod.getInstance());

        std[Stats.MAGIC_DEFENCE.ordinal()] = new Calculator();
        std[Stats.MAGIC_DEFENCE.ordinal()].addFunc(FuncMDefMod.getInstance());

        std[Stats.CRITICAL_RATE.ordinal()] = new Calculator();
        std[Stats.CRITICAL_RATE.ordinal()].addFunc(FuncAtkCritical.getInstance());

        std[Stats.MCRITICAL_RATE.ordinal()] = new Calculator();
        std[Stats.MCRITICAL_RATE.ordinal()].addFunc(FuncMAtkCritical.getInstance());

        std[Stats.ACCURACY_COMBAT.ordinal()] = new Calculator();
        std[Stats.ACCURACY_COMBAT.ordinal()].addFunc(FuncAtkAccuracy.getInstance());

        std[Stats.EVASION_RATE.ordinal()] = new Calculator();
        std[Stats.EVASION_RATE.ordinal()].addFunc(FuncAtkEvasion.getInstance());

        std[Stats.POWER_ATTACK_SPEED.ordinal()] = new Calculator();
        std[Stats.POWER_ATTACK_SPEED.ordinal()].addFunc(FuncPAtkSpeed.getInstance());

        std[Stats.MAGIC_ATTACK_SPEED.ordinal()] = new Calculator();
        std[Stats.MAGIC_ATTACK_SPEED.ordinal()].addFunc(FuncMAtkSpeed.getInstance());

        std[Stats.MOVE_SPEED.ordinal()] = new Calculator();
        std[Stats.MOVE_SPEED.ordinal()].addFunc(FuncMoveSpeed.getInstance());

        return std;
    }

    /**
     * Add basics stats functions to a player.<br>
     * <B><U>Concept</U>:</B><br>
     * A calculator is created to manage and dynamically calculate the effect of a character property (ex : MAX_HP, REGENERATE_HP_RATE...).<br>
     * In fact, each calculator is a table of Func object in which each Func represents a mathematics function: <br>
     * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<br>
     * @param player the player
     */
    public static void addFuncsToNewPlayer(PlayerInstance player) {
        player.addStatFuncs(Arrays.asList( //
                FuncMaxHpMul.getInstance(), //
                FuncMaxCpMul.getInstance(), //
                FuncMaxMpMul.getInstance(), //
                FuncPAtkMod.getInstance(), //
                FuncMAtkMod.getInstance(), //
                FuncPDefMod.getInstance(), //
                FuncMDefMod.getInstance(), //
                FuncAtkCritical.getInstance(), //
                FuncMAtkCritical.getInstance(), //
                FuncAtkAccuracy.getInstance(), //
                FuncAtkEvasion.getInstance(), //
                FuncPAtkSpeed.getInstance(), //
                FuncMAtkSpeed.getInstance(), //
                FuncMoveSpeed.getInstance()));
    }

    /**
     * Calculate the HP regen rate (base + modifiers).
     * @param cha
     * @return
     */
    public static double calcHpRegen(Entity cha) {
        double init = (cha.isPlayer()) ? cha.getTemplate().getBaseHpReg(cha.getLevel()) : cha.getTemplate().getBaseHpReg();
        double hpRegenMultiplier = character.hpRegenMultiplier();
        double hpRegenBonus = 0;

        if ((cha.isPlayer())) {
            PlayerInstance player = (PlayerInstance) cha;

            // Calculate Movement bonus
            if (player.isSitting()) {
                hpRegenMultiplier *= 1.5; // Sitting
            } else if (!player.isMoving()) {
                hpRegenMultiplier *= 1.1; // Staying
            } else if (player.isRunning()) {
                hpRegenMultiplier *= 0.7; // Running
            }

            // Add CON bonus
            init *= cha.getLevelMod() * BaseStats.CON.calcBonus(cha);
        }

        return (cha.calcStat(Stats.REGENERATE_HP_RATE, Math.max(1, init), null, null) * hpRegenMultiplier) + hpRegenBonus;
    }

    /**
     * Calculate the MP regen rate (base + modifiers).
     * @param cha
     * @return
     */
    public static double calcMpRegen(Entity cha) {
        double init = cha.isPlayer() ? cha.getTemplate().getBaseMpReg(cha.getLevel()) : cha.getTemplate().getBaseMpReg();
        double mpRegenMultiplier = character.mpRegenMultiplier();
        double mpRegenBonus = 0;

        if ((cha.isPlayer())) {
            PlayerInstance player = (PlayerInstance) cha;

            // Calculate Movement bonus
            if (player.isSitting()) {
                mpRegenMultiplier *= 1.5; // Sitting
            } else if (!player.isMoving()) {
                mpRegenMultiplier *= 1.1; // Staying
            } else if (player.isRunning()) {
                mpRegenMultiplier *= 0.7; // Running
            }

            // Add MEN bonus
            init *= cha.getLevelMod() * BaseStats.MEN.calcBonus(cha);
        }

        return (cha.calcStat(Stats.REGENERATE_MP_RATE, Math.max(1, init), null, null) * mpRegenMultiplier) + mpRegenBonus;
    }

    /**
     * Calculates the CP regeneration rate (base + modifiers).
     * @param player the player
     * @return the CP regeneration rate
     */
    public static double calcCpRegen(PlayerInstance player) {
        // With CON bonus
        final double init = player.getTemplate().getBaseCpReg(player.getLevel()) * player.getLevelMod() * BaseStats.CON.calcBonus(player);
        double cpRegenMultiplier = character.cpRegenMultiplier();
        if (player.isSitting()) {
            cpRegenMultiplier *= 1.5; // Sitting
        } else if (!player.isMoving()) {
            cpRegenMultiplier *= 1.1; // Staying
        } else if (player.isRunning()) {
            cpRegenMultiplier *= 0.7; // Running
        }
        return player.calcStat(Stats.REGENERATE_CP_RATE, Math.max(1, init), null, null) * cpRegenMultiplier;
    }


    /**
     * Calculated damage caused by ATTACK of attacker on target.
     * @param attacker player or NPC that makes ATTACK
     * @param target player or NPC, target of ATTACK
     * @param shld
     * @param crit if the ATTACK have critical success
     * @param ss if weapon item was charged by soulshot
     * @return
     */
    public static double calcPhysDam(Entity attacker, Entity target, byte shld, boolean crit, boolean ss) {
        double defence = target.getPDef(attacker);

        switch (shld) {
            case SHIELD_DEFENSE_SUCCEED:
                if (!character.shieldBlocks()) {
                    defence += target.getShldDef();

                }
                break;
            case SHIELD_DEFENSE_PERFECT_BLOCK:
                return 1;
        }

        final boolean isPvP = attacker.isPlayer() && target.isPlayer();
        double proximityBonus = attacker.isBehindTarget() ? 1.2 : attacker.isInFrontOfTarget() ? 1 : 1.1; // Behind: +20% - Side: +10%
        double damage = attacker.getPAtk(target);
        double ssboost = ss ? 2 : 1;

        if (isPvP) {
            // Defense bonuses in PvP fight
            defence *= target.calcStat(Stats.PVP_PHYSICAL_DEF, 1, null, null);
        }

        damage *= ssboost;

        if (crit) {
            // H5 Damage Formula
            damage = 2 * attacker.calcStat(Stats.CRITICAL_DAMAGE, 1, target, null) *
                    attacker.calcStat(Stats.CRITICAL_DAMAGE_POS, 1, target, null) *
                    target.calcStat(Stats.DEFENCE_CRITICAL_DAMAGE, 1, target, null) *
                    ((76 * damage * proximityBonus) / defence);
            damage += ((attacker.calcStat(Stats.CRITICAL_DAMAGE_ADD, 0, target, null) * 77) / defence);
            damage += target.calcStat(Stats.DEFENCE_CRITICAL_DAMAGE_ADD, 0, target, null);
        } else {
            damage = (76 * damage * proximityBonus) / defence;
        }

        // Weapon random damage
        damage *= attacker.getRandomDamageMultiplier();

        // Dmg bonuses in PvP fight
        if (isPvP) {
            damage *= attacker.calcStat(Stats.PVP_PHYSICAL_DMG, 1, null, null);
        }

        if (target.isEntity()) {
            if (target.getLevel() - attacker.getLevel() >= 2) {
                int lvlDiff = target.getLevel() - attacker.getLevel() - 1;

                if (crit) {
                    if (lvlDiff >= npc.lvlDifferenceCritDmgPenalty().length) {
                        damage *= npc.lvlDifferenceCritDmgPenalty()[npc.lvlDifferenceCritDmgPenalty().length - 1];
                    } else {
                        damage *= npc.lvlDifferenceCritDmgPenalty()[lvlDiff];
                    }
                } else {
                    if (lvlDiff >= npc.lvlDifferenceDmgPenalty().length) {
                        damage *= npc.lvlDifferenceDmgPenalty()[npc.
                                lvlDifferenceDmgPenalty().length - 1];
                    } else {
                        damage *= npc.lvlDifferenceDmgPenalty()[lvlDiff];
                    }
                }
            }
        }

        return Math.max(damage, 1);
    }

    /**
     * Returns true in case of critical hit
     * @param attacker
     * @param target
     * @return
     */
    public static boolean calcCrit(Entity attacker, Entity target) {
        double rate = attacker.getStat().calcStat(Stats.CRITICAL_RATE_POS,
                attacker.getStat().getCriticalHit(target, null));
        return (target.getStat().calcStat(Stats.DEFENCE_CRITICAL_RATE, rate, null, null)
                + target.getStat().calcStat(Stats.DEFENCE_CRITICAL_RATE_ADD, 0, null, null)) >
                Rnd.get(1000);
    }

    /**
     * Returns true in case of physical skill critical hit
     * @param attacker
     * @param target
     * @param criticalChance
     * @return
     */
    public static boolean calcSkillCrit(Entity attacker, Entity target, int criticalChance) {
        return (BaseStats.STR.calcBonus(attacker) * criticalChance) > (Rnd.nextDouble() * 100);
    }

    public static boolean calcMCrit(double mRate) {
        return mRate > Rnd.get(1000);
    }

    /**
     * Formula based on http://l2p.l2wh.com/nonskillattacks.html
     * @param attacker
     * @param target
     * @return {@code true} if hit missed (target evaded), {@code false} otherwise.
     */
    public static boolean calcHitMiss(Entity attacker, Entity target) {
        int chance = (80 + (2 * (attacker.getAccuracy() - target.getEvasionRate(attacker)))) * 10;

        // Get additional bonus from the conditions when you are attacking
        //chance *= HitConditionBonusData.getInstance().getConditionBonus(attacker, target);

        chance = Math.max(chance, 200);
        chance = Math.min(chance, 980);

        return chance < Rnd.get(1000);
    }

    /**
     * Returns:<br>
     * 0 = shield defense doesn't succeed<br>
     * 1 = shield defense succeed<br>
     * 2 = perfect block<br>
     * @param attacker
     * @param target
     * @param skill
     * @param sendSysMsg
     * @return
     */
    public static byte calcShldUse(Entity attacker, Entity target, Skill skill, boolean sendSysMsg) {
        DBItem item = target.getSecondaryWeaponItem();
        if (item == null) {
            return 0;
        }

        double shldRate = target.calcStat(Stats.SHIELD_RATE, 0, attacker, null) * BaseStats.DEX.calcBonus(target);
        if (shldRate <= 1e-6) {
            return 0;
        }

        int degreeside = (int) target.calcStat(Stats.SHIELD_DEFENCE_ANGLE, 0, null, null) + 120;
        if ((degreeside < 360) && (!target.isFacing(attacker, degreeside))) {
            return 0;
        }

        byte shldSuccess = SHIELD_DEFENSE_FAILED;
        // if attacker
        // if attacker use bow and target wear shield, shield block rate is multiplied by 1.3 (30%)
        DBWeapon at_weapon = attacker.getActiveWeaponItem();
        if ((at_weapon != null) && (at_weapon.getType() == WeaponType.bow)) {
            shldRate *= 1.3;
        }

        if ((shldRate > 0) && ((100 - character.shieldPerfectBlockRate()) < Rnd.get(100))) {
            shldSuccess = SHIELD_DEFENSE_PERFECT_BLOCK;
        } else if (shldRate > Rnd.get(100)) {
            shldSuccess = SHIELD_DEFENSE_SUCCEED;
        }

        if (sendSysMsg && target.isPlayer()) {
            PlayerInstance enemy = (PlayerInstance) target;

            switch (shldSuccess) {
                case SHIELD_DEFENSE_SUCCEED:
                    enemy.sendPacket(SystemMessageId.SHIELD_DEFENCE_SUCCESSFULL);
                    break;
                case SHIELD_DEFENSE_PERFECT_BLOCK:
                    enemy.sendPacket(SystemMessageId.YOUR_EXCELLENT_SHIELD_DEFENSE_WAS_A_SUCCESS);
                    break;
            }
        }
        return shldSuccess;
    }

    public static byte calcShldUse(Entity attacker, Entity target, Skill skill) {
        return calcShldUse(attacker, target, skill, true);
    }

    public static byte calcShldUse(Entity attacker, Entity target) {
        return calcShldUse(attacker, target,
                null, true);
    }

    public static double calcLvlBonusMod(Entity attacker, Entity target, Skill skill) {
        int attackerLvl = skill.getMagicLevel() > 0 ? skill.getMagicLevel() : attacker.getLevel();
        double skillLvlBonusRateMod = 1 + (skill.getLvlBonusRate() / 100.);
        double lvlMod = 1 + ((attackerLvl - target.getLevel()) / 100.);
        return skillLvlBonusRateMod * lvlMod;
    }

    public static double calcManaDam(Entity attacker, Entity target, Skill skill, byte shld, boolean sps,
                                     boolean bss, boolean mcrit, double power) {
        // Formula: (SQR(M.Atk)*Power*(Target Max MP/97))/M.Def
        double mAtk = attacker.getMAtk(target, skill);
        double mDef = target.getMDef(attacker, skill);
        double mp = target.getMaxMp();

        switch (shld) {
            case SHIELD_DEFENSE_SUCCEED:
                mDef += target.getShldDef();
                break;
            case SHIELD_DEFENSE_PERFECT_BLOCK: // perfect block
                return 1;
        }

        // Bonus Spiritshot
        mAtk *= bss ? 4 : sps ? 2 : 1;

        double damage = (Math.sqrt(mAtk) * power * (mp / 97)) / mDef;

        if (target.isEntity()) {
            if (target.getLevel() - attacker.getLevel() >= 2) {
                int lvlDiff = target.getLevel() - attacker.getLevel() - 1;
                if (lvlDiff >= npc.lvlDifferenceSkillDmgPenalty().length) {
                    damage *= npc.lvlDifferenceSkillDmgPenalty()[npc.lvlDifferenceSkillDmgPenalty().length - 1];
                } else {
                    damage *= npc.lvlDifferenceSkillDmgPenalty()[lvlDiff];
                }
            }
        }

        // Failure calculation
        if (character.magicFailures() && !calcMagicSuccess(attacker, target, skill)) {
            if (attacker.isPlayer()) {
                SystemMessagePacket sm = SystemMessagePacket.getSystemMessage(
                        SystemMessageId.DAMAGE_DECREASED_BECAUSE_C1_RESISTED_C2_MAGIC);
                sm.addCharName(target);
                sm.addCharName(attacker);
                ((PlayerInstance) attacker).sendPacket(sm);
                damage /= 2;
            }

            if (target.isPlayer()) {
                SystemMessagePacket sm2 = SystemMessagePacket.getSystemMessage(
                        SystemMessageId.C1_WEAKLY_RESISTED_C2_MAGIC);
                sm2.addCharName(target);
                sm2.addCharName(attacker);
                ((PlayerInstance) target).sendPacket(sm2);
            }
        }

        if (mcrit) {
            damage *= 3;
            if (attacker.isPlayer()) {
                ((PlayerInstance) attacker).sendPacket(SystemMessageId.CRITICAL_HIT_MAGIC);
            }
        }
        return damage;
    }

    public static boolean calcMagicSuccess(Entity attacker, Entity target, Skill skill) {
        int lvlDifference = (target.getLevel() - (skill.getMagicLevel() > 0 ? skill.getMagicLevel() : attacker.getLevel()));
        double lvlModifier = Math.pow(1.3, lvlDifference);
        double targetModifier = 1.00;
        if (target.isEntity() &&
                (attacker != null) &&
                ((target.getLevel() - attacker.getLevel()) >= 3)) {
            int lvlDiff = target.getLevel() - attacker.getLevel() - 2;
            if (lvlDiff >= npc.lvlDifferenceSkillChancePenalty().length) {
                targetModifier = npc.lvlDifferenceSkillChancePenalty()[npc.lvlDifferenceSkillChancePenalty().length - 1];
            } else {
                targetModifier = npc.lvlDifferenceSkillChancePenalty()[lvlDiff];
            }
        }

        // general magic resist
        final double resModifier = target.calcStat(Stats.MAGIC_SUCCESS_RES, 1, null, skill);
        int rate = 100 - Math.round((float) (lvlModifier * targetModifier * resModifier));

//        if (attacker.isDebug()) {
//            final StatsSet set = new StatsSet();
//            set.set("lvlDifference", lvlDifference);
//            set.set("lvlModifier", lvlModifier);
//            set.set("resModifier", resModifier);
//            set.set("targetModifier", targetModifier);
//            set.set("rate", rate);
//            Debug.sendSkillDebug(attacker, target, skill, set);
//        }

        return (Rnd.get(100) < rate);
    }

    /**
     * Calculate Probability in following effects:<br>
     * TargetCancel,<br>
     * TargetMeProbability,<br>
     * SkillTurning,<br>
     * Betray,<br>
     * Bluff,<br>
     * DeleteHate,<br>
     * RandomizeHate,<br>
     * DeleteHateOfMe,<br>
     * TransferHate,<br>
     * Confuse<br>
     * @param baseChance chance from effect parameter
     * @param attacker
     * @param target
     * @param skill
     * @return chance for effect to succeed
     */
    public static boolean calcProbability(double baseChance, Entity attacker, Entity target, Skill skill) {
        return Rnd.get(100) < ((((skill.getMagicLevel() + baseChance) - target.getLevel()) + 30) - target.getINT());
    }

    /**
     * Calculates karma lost upon death.
     * @param player
     * @param exp
     * @return the amount of karma player has loosed.
     */
    public static int calculateKarmaLost(PlayerInstance player, long exp) {
        double karmaLooseMul = calcKarmaLooseMul(player.getLevel());
        if (exp > 0) // Received exp
        {
            exp /= rates.rateKarmaLost();
        }
        return (int) ((Math.abs(exp) / karmaLooseMul) / 30);
    }

    private static float calcKarmaLooseMul(int level) {
            if (level < 1 || level > 85) {
                throw new IllegalArgumentException("Level must be between 1 and 85");
            }

            if (level <= 10) {
                return 0.18f * level + 1.5f;
            } else if (level <= 20) {
                return 0.275f * level - 0.4f;
            } else if (level <= 30) {
                return 0.22f * level + 2.4f;
            } else if (level <= 40) {
                return 0.175f * level + 6f;
            } else if (level <= 50) {
                return 0.155f * level + 9.3f;
            } else if (level <= 60) {
                return 0.165f * level + 11.5f;
            } else if (level <= 70) {
                return 0.23f * level + 10.3f;
            } else {
                return 0.0015f * level * level + 0.13f * level + 19;
            }
    }

    /**
     * Calculates karma gain upon playable kill.</br>
     * Updated to High Five on 10.09.2014 by Zealar tested in retail.
     * @param pkCount
     * @param isSummon
     * @return karma points that will be added to the player.
     */
    public static int calculateKarmaGain(int pkCount, boolean isSummon) {
        int result = 43200;

        if (isSummon) {
            result = (int) ((((pkCount * 0.375) + 1) * 60) * 4) - 150;

            if (result > 10800) {
                return 10800;
            }
        }

        if (pkCount < 99) {
            result = (int) ((((pkCount * 0.5) + 1) * 60) * 12);
        } else if (pkCount < 180) {
            result = (int) ((((pkCount * 0.125) + 37.75) * 60) * 12);
        }

        return result;
    }
}

