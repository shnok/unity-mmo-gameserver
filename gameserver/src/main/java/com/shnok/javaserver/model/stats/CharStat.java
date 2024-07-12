package com.shnok.javaserver.model.stats;

import com.shnok.javaserver.db.entity.DBWeapon;
import com.shnok.javaserver.enums.MoveType;
import com.shnok.javaserver.enums.PlayerCondOverride;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;

import static com.shnok.javaserver.config.Configuration.character;

public class CharStat {
    private final Entity _activeChar;
    private int _level = 1;

    public CharStat(Entity activeChar) {
        _activeChar = activeChar;
    }

    public final float calcStat(Stats stat, float init) {
        return calcStat(stat, init, null, null);
    }

    /**
     * Calculate the new value of the state with modifiers that will be applied on the targeted Entity.<BR>
     * <B><U> Concept</U> :</B><BR A Entity owns a table of Calculators called <B>_calculators</B>. Each Calculator (a calculator per state) own a table of Func object. A Func object is a mathematical function that permit to calculate the modifier of a state (ex : REGENERATE_HP_RATE...) : <BR>
     * FuncAtkAccuracy -> Math.sqrt(_player.getDEX())*6+_player.getLevel()<BR>
     * When the calc method of a calculator is launched, each mathematical function is called according to its priority <B>_order</B>.<br>
     * Indeed, Func with lowest priority order is executed first and Funcs with the same order are executed in unspecified order.<br>
     * The result of the calculation is stored in the value property of an Env class instance.<br>
     * @param stat The stat to calculate the new value with modifiers
     * @param initVal The initial value of the stat before applying modifiers
     * @param target The Entity whose properties will be used in the calculation (ex : CON, INT...)
     * @param skill The L2Skill whose properties will be used in the calculation (ex : Level...)
     * @return
     */
    public final float calcStat(Stats stat, float initVal, Entity target, Skill skill) {
        float value = initVal;
        if (stat == null) {
            return value;
        }

        final int id = stat.ordinal();
        final Calculator c = _activeChar.getCalculators()[id];

        // If no Func object found, no modifier is applied
        if ((c == null) || (c.size() == 0)) {
            return value;
        }

        // Launch the calculation
        value = c.calc(_activeChar, target, skill, value);

        System.out.println("Calc stat: [" + stat + "]=" + value);

        // avoid some troubles with negative stats (some stats should never be negative)
        if (value <= 0) {
            switch (stat) {
                case MAX_HP:
                case MAX_MP:
                case MAX_CP:
                case MAGIC_DEFENCE:
                case POWER_DEFENCE:
                case POWER_ATTACK:
                case MAGIC_ATTACK:
                case POWER_ATTACK_SPEED:
                case MAGIC_ATTACK_SPEED:
                case SHIELD_DEFENCE:
                case STAT_CON:
                case STAT_DEX:
                case STAT_INT:
                case STAT_MEN:
                case STAT_STR:
                case STAT_WIT:
                    value = 1.0f;
            }
        }
        return value;
    }

    /**
     * @return the Accuracy (base+modifier) of the Entity in function of the Weapon Expertise Penalty.
     */
    public int getPAccuracy() {
        return (int) Math.round(calcStat(Stats.POWER_ACCURACY_COMBAT, 0, null, null));
    }

    /**
     * @return the Accuracy (base+modifier) of the Entity in function of the Weapon Expertise Penalty.
     */
    public int getMAccuracy() {
        return (int) Math.round(calcStat(Stats.MAGIC_ACCURACY_COMBAT, 0, null, null));
    }

    public Entity getActiveChar() {
        return _activeChar;
    }

    /**
     * @return the Attack Speed multiplier (base+modifier) of the Entity to get proper animations.
     */
    public final float getAttackSpeedMultiplier() {
        return (float) (((1.1) * getPAtkSpd()) / _activeChar.getTemplate().getBasePAtkSpd());
    }

    /**
     * @return the CON of the Entity (base+modifier).
     */
    public final int getCON() {
        return (int) calcStat(Stats.STAT_CON, _activeChar.getTemplate().getBaseCON());
    }

    /**
     * @param target
     * @param init
     * @return the Critical Damage rate (base+modifier) of the Entity.
     */
    public final float getCriticalDmg(Entity target, float init) {
        return calcStat(Stats.CRITICAL_DAMAGE, init, target, null);
    }

    /**
     * @param target
     * @param skill
     * @return the Critical Hit rate (base+modifier) of the Entity.
     */
    public int getCriticalHit(Entity target, Skill skill) {
        float val = (int) calcStat(Stats.CRITICAL_RATE, _activeChar.getTemplate().getBaseCritRate(), target, skill);
        if (!_activeChar.canOverrideCond(PlayerCondOverride.MAX_STATS_VALUE)) {
            val = Math.min(val, character.getMaxPCritRate());
        }
        return (int) (val + .5);
    }

    /**
     * @param base
     * @return the Critical Hit Pos rate of the Entity
     */
    public int getCriticalHitPos(int base) {
        return (int) calcStat(Stats.CRITICAL_RATE_POS, base);
    }

    /**
     * @return the DEX of the Entity (base+modifier).
     */
    public final int getDEX() {
        return (int) calcStat(Stats.STAT_DEX, _activeChar.getTemplate().getBaseDEX());
    }

    /**
     * @param target
     * @return the Attack Evasion rate (base+modifier) of the Entity.
     */
    public int getPEvasionRate(Entity target) {
        int val = (int) Math.round(calcStat(Stats.POWER_EVASION_RATE, 0, target, null));
        if (!_activeChar.canOverrideCond(PlayerCondOverride.MAX_STATS_VALUE)) {
            val = Math.min(val, character.getMaxEvasion());
        }
        return val;
    }

    /**
     * @param target
     * @return the Attack Evasion rate (base+modifier) of the Entity.
     */
    public int getMEvasionRate(Entity target) {
        int val = (int) Math.round(calcStat(Stats.MAGIC_EVASION_RATE, 0, target, null));
        if (!_activeChar.canOverrideCond(PlayerCondOverride.MAX_STATS_VALUE)) {
            val = Math.min(val, character.getMaxEvasion());
        }
        return val;
    }

    /**
     * @return the INT of the Entity (base+modifier).
     */
    public int getINT() {
        return (int) calcStat(Stats.STAT_INT, _activeChar.getTemplate().getBaseINT());
    }

    public int getLevel() {
        return _level;
    }

    public void setLevel(int value) {
        _level = value;
    }

    /**
     * @param skill
     * @return the Magical Attack range (base+modifier) of the Entity.
     */
    public final float getMagicalAttackRange(Skill skill) {
        if (skill != null) {
            return (int) calcStat(Stats.MAGIC_ATTACK_RANGE, skill.getCastRange(), null, skill);
        }

        return _activeChar.getTemplate().getBaseAtkRange();
    }

    public int getMaxCp() {
        return (int) calcStat(Stats.MAX_CP, _activeChar.getTemplate().getBaseCpMax());
    }

    public int getMaxRecoverableCp() {
        return (int) calcStat(Stats.MAX_RECOVERABLE_CP, getMaxCp());
    }

    public int getMaxHp() {
        return (int) calcStat(Stats.MAX_HP, _activeChar.getTemplate().getBaseHpMax());
    }

    public int getMaxRecoverableHp() {
        return (int) calcStat(Stats.MAX_RECOVERABLE_HP, getMaxHp());
    }

    public int getMaxMp() {
        return (int) calcStat(Stats.MAX_MP, _activeChar.getTemplate().getBaseMpMax());
    }

    public int getMaxRecoverableMp() {
        return (int) calcStat(Stats.MAX_RECOVERABLE_MP, getMaxMp());
    }

    /**
     * Return the MAtk (base+modifier) of the Entity.<br>
     * <B><U>Example of use</U>: Calculate Magic damage
     * @param target The Entity targeted by the skill
     * @param skill The L2Skill used against the target
     * @return
     */
    public float getMAtk(Entity target, Skill skill) {
        float bonusAtk = 1;
        // Calculate modifiers Magic Attack
        return calcStat(Stats.MAGIC_ATTACK, _activeChar.getTemplate().getBaseMAtk() * bonusAtk, target, skill);
    }

    /**
     * @return the MAtk Speed (base+modifier) of the Entity in function of the Armour Expertise Penalty.
     */
    public int getMAtkSpd() {
        float bonusSpdAtk = 1;

        float val = calcStat(Stats.MAGIC_ATTACK_SPEED, _activeChar.getTemplate().getBaseMAtkSpd() * bonusSpdAtk);
        if (!_activeChar.canOverrideCond(PlayerCondOverride.MAX_STATS_VALUE)) {
            val = Math.min(val, character.getMaxMAtkSpeed());
        }
        return (int) val;
    }

    /**
     * @param target
     * @param skill
     * @return the Magic Critical Hit rate (base+modifier) of the Entity.
     */
    public final int getMCriticalHit(Entity target, Skill skill) {
        int val = (int) calcStat(Stats.MCRITICAL_RATE, 1, target, skill) * 10;
        if (!_activeChar.canOverrideCond(PlayerCondOverride.MAX_STATS_VALUE)) {
            val = Math.min(val, character.getMaxMCritRate());
        }
        return val;
    }

    /**
     * <B><U>Example of use </U>: Calculate Magic damage.
     * @param target The Entity targeted by the skill
     * @param skill The L2Skill used against the target
     * @return the MDef (base+modifier) of the Entity against a skill in function of abnormal effects in progress.
     */
    public float getMDef(Entity target, Skill skill) {
        // Get the base MDef of the Entity
        float defence = _activeChar.getTemplate().getBaseMDef();

        // Calculate modifiers Magic Attack
        return calcStat(Stats.MAGIC_DEFENCE, defence, target, skill);
    }

    /**
     * @return the MEN of the Entity (base+modifier).
     */
    public final int getMEN() {
        return (int) calcStat(Stats.STAT_MEN, _activeChar.getTemplate().getBaseMEN());
    }

    public float getMovementSpeedMultiplier() {
        float baseSpeed;
//        if (_activeChar.isInsideZone(ZoneId.WATER)) {
//            baseSpeed = getBaseMoveSpeed(_activeChar.isRunning() ? MoveType.FAST_SWIM : MoveType.SLOW_SWIM);
//        } else {
            baseSpeed = getBaseMoveSpeed(_activeChar.isRunning() ? MoveType.RUN : MoveType.WALK);
//        }
        return getMoveSpeed() * (1.f / baseSpeed);
    }

    /**
     * @return the RunSpeed (base+modifier) of the Entity in function of the Armour Expertise Penalty.
     */
    public float getRunSpeed() {
//        final float baseRunSpd = _activeChar.isInsideZone(ZoneId.WATER) ? getSwimRunSpeed() : getBaseMoveSpeed(MoveType.RUN);
        final float baseRunSpd = getBaseMoveSpeed(MoveType.RUN);
        if (baseRunSpd <= 0) {
            return 0;
        }

        return calcStat(Stats.MOVE_SPEED, baseRunSpd, null, null);
    }

    /**
     * @return the WalkSpeed (base+modifier) of the Entity.
     */
    public float getWalkSpeed() {
//        final float baseWalkSpd = _activeChar.isInsideZone(ZoneId.WATER) ? getSwimWalkSpeed() : getBaseMoveSpeed(MoveType.WALK);
        final float baseWalkSpd = getBaseMoveSpeed(MoveType.WALK);
        if (baseWalkSpd <= 0) {
            return 0;
        }

        return calcStat(Stats.MOVE_SPEED, baseWalkSpd);
    }

    /**
     * @return the SwimRunSpeed (base+modifier) of the Entity.
     */
    public float getSwimRunSpeed() {
        final float baseRunSpd = getBaseMoveSpeed(MoveType.FAST_SWIM);
        if (baseRunSpd <= 0) {
            return 0;
        }

        return calcStat(Stats.MOVE_SPEED, baseRunSpd, null, null);
    }

    /**
     * @return the SwimWalkSpeed (base+modifier) of the Entity.
     */
    public float getSwimWalkSpeed() {
        final float baseWalkSpd = getBaseMoveSpeed(MoveType.SLOW_SWIM);
        if (baseWalkSpd <= 0) {
            return 0;
        }

        return calcStat(Stats.MOVE_SPEED, baseWalkSpd);
    }

    /**
     * @param type movement type
     * @return the base move speed of given movement type.
     */
    public float getBaseMoveSpeed(MoveType type) {
        return _activeChar.getTemplate().getBaseMoveSpeed(type);
    }

    /**
     * @return the RunSpeed (base+modifier) or WalkSpeed (base+modifier) of the Entity in function of the movement type.
     */
    public float getMoveSpeed() {
//        if (_activeChar.isInsideZone(ZoneId.WATER)) {
//            return _activeChar.isRunning() ? getSwimRunSpeed() : getSwimWalkSpeed();
//        }
        return _activeChar.isRunning() ? getRunSpeed() : getWalkSpeed();
    }

    /**
     * @param skill
     * @return the MReuse rate (base+modifier) of the Entity.
     */
    public final float getMReuseRate(Skill skill) {
        return calcStat(Stats.MAGIC_REUSE_RATE, 1, null, skill);
    }

    /**
     * @param target
     * @return the PAtk (base+modifier) of the Entity.
     */
    public float getPAtk(Entity target) {
        float bonusAtk = 1;
        return calcStat(Stats.POWER_ATTACK, _activeChar.getTemplate().getBasePAtk() * bonusAtk, target, null);
    }

    /**
     * @return the PAtk Speed (base+modifier) of the Entity in function of the Armour Expertise Penalty.
     */
    public float getPAtkSpd() {
        float bonusAtk = 1;
        return Math.round(calcStat(Stats.POWER_ATTACK_SPEED, _activeChar.getTemplate().getBasePAtkSpd() * bonusAtk, null, null));
    }

    /**
     * @param target
     * @return the PDef (base+modifier) of the Entity.
     */
    public float getPDef(Entity target) {
        return calcStat(Stats.POWER_DEFENCE, _activeChar.getTemplate().getBasePDef(), target, null);
    }

    /**
     * @return the Physical Attack range (base+modifier) of the Entity.
     */
    public final float getPhysicalAttackRange() {
        final DBWeapon weapon = _activeChar.getActiveWeaponItem();
        float baseAttackRange;

        if (weapon != null) {
            baseAttackRange = weapon.getBaseAttackRange();
        } else {
            baseAttackRange = _activeChar.getTemplate().getBaseAtkRange();
        }

        return (int) calcStat(Stats.POWER_ATTACK_RANGE, baseAttackRange, null, null);
    }

    public int getPhysicalAttackAngle() {
        final DBWeapon weapon = _activeChar.getActiveWeaponItem();
        final int baseAttackAngle;
        if (weapon != null) {
            baseAttackAngle = weapon.getBaseAttackAngle();
        } else {
            baseAttackAngle = 120;
        }
        return baseAttackAngle;
    }

    /**
     * @param target
     * @return the weapon reuse modifier.
     */
    public final float getWeaponReuseModifier(Entity target) {
        return calcStat(Stats.ATK_REUSE, 1, target, null);
    }

    /**
     * @return the ShieldDef rate (base+modifier) of the Entity.
     */
    public final int getShldDef() {
        return (int) calcStat(Stats.SHIELD_DEFENCE, 0);
    }

    /**
     * @return the STR of the Entity (base+modifier).
     */
    public final int getSTR() {
        return (int) calcStat(Stats.STAT_STR, _activeChar.getTemplate().getBaseSTR());
    }

    /**
     * @return the WIT of the Entity (base+modifier).
     */
    public final int getWIT() {
        return (int) calcStat(Stats.STAT_WIT, _activeChar.getTemplate().getBaseWIT());
    }

    /**
     * Gets the maximum buff count.
     * @return the maximum buff count
     */
    public int getMaxBuffCount() {
        return (int) calcStat(Stats.ENLARGE_ABNORMAL_SLOT, character.maxBuffAmount());
    }
}
