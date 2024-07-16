package com.shnok.javaserver.model.stats;

import com.shnok.javaserver.enums.MoveType;
import com.shnok.javaserver.enums.PlayerCondOverride;
import com.shnok.javaserver.model.object.entity.PlayerInstance;

import static com.shnok.javaserver.config.Configuration.character;

public class PlayerStat extends CharStat {
    private int oldMaxHp; // stats watch
    private int oldMaxMp; // stats watch
    private int oldMaxCp; // stats watch
    private long startingXp;
    private boolean cloakSlot = false;

    public PlayerStat(PlayerInstance activeChar) {
        super(activeChar);
    }

    @Override
    public final int getMaxCp() {
        // Get the Max CP (base+modifier) of the L2PcInstance
        int val = (getActiveChar() == null) ? 1 : (int) calcStat(Stats.MAX_CP, getActiveChar().getTemplate().getBaseCpMax(getActiveChar().getLevel()));
        if (val != oldMaxCp) {
            oldMaxCp = val;

            // Launch a regen task if the new Max CP is higher than the old one
            if (getActiveChar().getStatus().getCurrentCp() != val) {
                getActiveChar().getStatus().setCurrentCp(getActiveChar().getStatus().getCurrentCp()); // trigger start of regeneration
            }
        }
        return val;
    }

    @Override
    public final int getMaxHp() {
        // Get the Max HP (base+modifier) of the L2PcInstance
        int val = (getActiveChar() == null) ? 1 : (int) calcStat(Stats.MAX_HP, getActiveChar().getTemplate().getBaseHpMax(getActiveChar().getLevel()));
        if (val != oldMaxHp) {
            oldMaxHp = val;

            // Launch a regen task if the new Max HP is higher than the old one
            if (getActiveChar().getStatus().getCurrentHp() != val) {
                getActiveChar().getStatus().setCurrentHp(getActiveChar().getStatus().getCurrentHp()); // trigger start of regeneration
            }
        }

        return val;
    }

    @Override
    public final int getMaxMp() {
        // Get the Max MP (base+modifier) of the L2PcInstance
        int val = (getActiveChar() == null) ? 1 : (int) calcStat(Stats.MAX_MP, getActiveChar().getTemplate().getBaseMpMax(getActiveChar().getLevel()));

        if (val != oldMaxMp) {
            oldMaxMp = val;

            // Launch a regen task if the new Max MP is higher than the old one
            if (getActiveChar().getStatus().getCurrentMp() != val) {
                getActiveChar().getStatus().setCurrentMp(getActiveChar().getStatus().getCurrentMp()); // trigger start of regeneration
            }
        }

        return val;
    }

    /**
     * @param type movement type
     * @return the base move speed of given movement type.
     */
    @Override
    public float getBaseMoveSpeed(MoveType type) {
        return super.getBaseMoveSpeed(type);
    }

    @Override
    public float getRunSpeed() {
        float val = super.getRunSpeed() + character.runSpeedBoost();

        // Apply max run speed cap.
        if ((val > character.getMaxRunSpeed()) && !getActiveChar().canOverrideCond(PlayerCondOverride.MAX_STATS_VALUE)) {
            return character.getMaxRunSpeed();
        }

        return val;
    }

    @Override
    public float getWalkSpeed() {
        float val = super.getWalkSpeed() + character.runSpeedBoost();

        // Apply max run speed cap.
        if ((val > character.getMaxRunSpeed()) && !getActiveChar().canOverrideCond(PlayerCondOverride.MAX_STATS_VALUE)) {
            return character.getMaxRunSpeed();
        }

        return val;
    }

    @Override
    public float getPAtkSpd() {
        float val = super.getPAtkSpd();

        if ((val > character.getMaxPAtkSpeed()) && !getActiveChar().canOverrideCond(PlayerCondOverride.MAX_STATS_VALUE)) {
            return character.getMaxPAtkSpeed();
        }

        return val;
    }

    public double getExpBonusMultiplier() {
        double bonus = 1.0;
        double hunting = 1.0;

        // Bonus exp from skills
        double bonusExp = 1 + (calcStat(Stats.BONUS_EXP, 0, null, null) / 100);

        if (hunting > 1.0) {
            bonus += (hunting - 1);
        }
        if (bonusExp > 1) {
            bonus += (bonusExp - 1);
        }

        // Check for abnormal bonuses
        bonus = Math.max(bonus, 1);
        bonus = Math.min(bonus, character.getMaxExpBonus());

        return bonus;
    }

    public double getSpBonusMultiplier() {
        double bonus = 1.0;
        double hunting = 1.0;

        // Bonus sp from skills
        double bonusSp = 1 + (calcStat(Stats.BONUS_SP, 0, null, null) / 100);

        if (hunting > 1.0f) {
            bonus += (hunting - 1);
        }
        if (bonusSp > 1) {
            bonus += (bonusSp - 1);
        }

        // Check for abnormal bonuses
        bonus = Math.max(bonus, 1);
        bonus = Math.min(bonus, character.getMaxSpBonus());

        return bonus;
    }

    public int getMaxLevel() {
        return character.getMaxPlayerLevel();
    }

    public int getMaxExpLevel() {
        return character.getMaxPlayerLevel() + 1;
    }

    public final PlayerInstance getActiveChar() {
        return (PlayerInstance) super.getActiveChar();
    }
}
