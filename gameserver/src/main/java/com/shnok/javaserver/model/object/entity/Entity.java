package com.shnok.javaserver.model.object.entity;

import com.shnok.javaserver.db.entity.DBArmor;
import com.shnok.javaserver.db.entity.DBWeapon;
import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.dto.external.serverpackets.*;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.enums.PlayerAction;
import com.shnok.javaserver.enums.PlayerCondOverride;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.model.WorldRegion;
import com.shnok.javaserver.model.knownlist.EntityKnownList;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.MovableObject;
import com.shnok.javaserver.model.skills.FormulasLegacy;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.Calculator;
import com.shnok.javaserver.model.stats.CharStat;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.model.template.EntityTemplate;
import com.shnok.javaserver.security.Rnd;
import com.shnok.javaserver.service.GameTimeControllerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.thread.entity.ScheduleHitTask;
import com.shnok.javaserver.thread.entity.ScheduleNotifyAITask;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

import static com.shnok.javaserver.model.stats.Stats.NUM_STATS;

/**
 * This class represents all entities in the world.<BR>
 * <BR>
 * Such as : Players or AIs<BR>
 * <BR>
 */

@Log4j2
@Getter
@Setter
public abstract class Entity extends MovableObject {
    /** Table of Calculators containing all used calculator */
    private Calculator[] calculators;

    /** Table of calculators containing all standard NPC calculator (ex : ACCURACY_COMBAT, EVASION_RATE) */
    private static final Calculator[] NPC_STD_CALCULATOR = Formulas.getStdNPCCalculators();
    protected long exceptions = 0L;
    protected Status status;
    protected long attackEndTime;
    protected long attackHitTime;
    protected long castEndTime;
    protected long channelEndTime;
    protected boolean dead;
    private ScheduledFuture<?> scheduledAttack;

    public Entity(int id, EntityTemplate template) {
        super(id);

        this.template = template;

        initCharStat();
        initCharStatus();

        if (isNpc()) {
            calculators = NPC_STD_CALCULATOR;
        } else {
            calculators = new Calculator[NUM_STATS];
        }
    }

    public EntityTemplate getTemplate() {
        return template;
    }

    /**
     * Initializes the CharStatus class of the L2Object, is overwritten in classes that require a different CharStatus Type.<br>
     * Removes the need for instanceof checks.
     */
    public void initCharStatus() {
        status = new Status(this);
    }

    public void setStatus(Status value) {
        status = value;
    }

    public void doDie(Entity attacker) {
        abortAttack();
        abortCast();
        stopAllTimers();

        setCanMove(false);
        setDead(true);

        log.debug("[{}] Entity died", getId());
        if (ai != null) {
            ai.notifyEvent(Event.DEAD);
        }
    }

    public boolean doAttack(Entity target) {
        if(scheduledAttack != null) {
            scheduledAttack.cancel(true);
        }

        // Extra target verification at each loop
        if (target == null || target.isDead() || !getKnownList().knowsObject(target)
                || getAi().getIntention() != Intention.INTENTION_ATTACK || isDead()) {
            getAi().notifyEvent(Event.CANCEL);
            sendPacket(new ActionFailedPacket(PlayerAction.AutoAttack.getValue()));
            return false;
        }

        // Check if attack animation is finished
//        if (!canAttack()) {
//            log.warn("[{}] Trying to attack when the last attack animation was not finished.", getId());
//            return;
//        }
        if (isAnimationLocked()) {
            log.warn("[{}] Trying to attack when the last attack animation was not finished.", getId());
            return false;
        }

        getAi().clientStartAutoAttack();

        // Get the Attack Speed of the npc (delay (in milliseconds) before next attack)
        int timeAtk = calculateTimeBetweenAttacks();
        // the hit is calculated to happen halfway to the animation
        int timeToHit = timeAtk / 2;
        int cooldown = calculateCooldown();

        attackEndTime = GameTimeControllerService.getInstance().getGameTicks();
        attackEndTime += (timeAtk / GameTimeControllerService.getInstance().getTickDurationMs());
        attackEndTime -= 1;

        attackHitTime = GameTimeControllerService.getInstance().getGameTicks();
        attackHitTime += (timeAtk / 2 / GameTimeControllerService.getInstance().getTickDurationMs());
        attackHitTime -= 1;

         //TODO handle soulshots
//        ApplyDamagePacket attack = new ApplyDamagePacket(this, target, isChargedShot(ShotType.SOULSHOTS),
//                (weaponItem != null) ? weaponItem.getItemGradeSPlus().getId() : 0);
        ApplyDamagePacket attack = new ApplyDamagePacket(this.getId(), false, (byte) 0);

        //TODO update heading server side
        //setHeading(VectorUtils.calculateHeadingFrom(this, target));

        // Start hit task
        boolean hit;
        DBWeapon weaponItem = getActiveWeaponItem();
        //TODO use functions according to weapon type
        hit = doAttackHitSimple(attack, target, timeToHit);

        log.debug("AtkSpd: {} Attack duration: {}ms", getTemplate().getBasePAtkSpd(), timeAtk);

        if(!hit) {
            //abortAttack();
        } else {
            // charge soulshot
        }

        scheduledAttack = ThreadPoolManagerService.getInstance().scheduleAi(new ScheduleNotifyAITask(getAi(), Event.READY_TO_ACT),
                timeAtk + cooldown);

        return true;
    }

    /**
     * Launch a simple attack.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Calculate if hit is missed or not</li>
     * <li>If hit isn't missed, calculate if shield defense is efficient</li>
     * <li>If hit isn't missed, calculate if hit is critical</li>
     * <li>If hit isn't missed, calculate physical damages</li>
     * <li>Create a new hit task with Medium priority<qa/li>
     * <li>Add this hit to the Server-Client packet Attack</li>
     * </ul>
     * @param attack Server->Client packet Attack in which the hit will be added
     * @param target The L2Character targeted
     * @param sAtk
     * @return True if the hit isn't missed
     */
    private boolean doAttackHitSimple(ApplyDamagePacket attack, Entity target, int sAtk) {
        return doAttackHitSimple(attack, target, 100, sAtk);
    }

    private boolean doAttackHitSimple(ApplyDamagePacket attack, Entity target, double attackpercent, int sAtk) {
        int damage1 = 0;
        byte shld1 = 0;
        boolean crit1 = false;

        // Calculate if hit is missed or not
        boolean miss1 = Formulas.calcHitMiss(this, target);

        // Check if hit isn't missed
        if (!miss1) {
            // Calculate if shield defense is efficient
            shld1 = Formulas.calcShldUse(this, target);

            // Calculate if hit is critical
            crit1 = Formulas.calcCrit(this, target);

            // Calculate physical damages
            damage1 = (int) Formulas.calcPhysDam(this, target, shld1, crit1, attack.hasSoulshot());

            if (attackpercent != 100) {
                damage1 = (int) ((damage1 * attackpercent) / 100);
            }
        }

        // Create a new hit task with Medium priority
        ThreadPoolManagerService.getInstance().scheduleAi(
                new ScheduleHitTask(attack, this, target, damage1, crit1, miss1, attack.hasSoulshot(), shld1), sAtk);
        //ThreadPoolManager.getInstance().scheduleAi(new HitTask(this, target, damage1, crit1, miss1, attack.hasSoulshot(), shld1), sAtk);

        // Add this hit to the Server-Client packet Attack
        attack.addHit(target, damage1, miss1, crit1, shld1);

        // Return true if hit isn't missed
        return !miss1;
    }

    /**
     * Manage hit process (called by Hit Task).<BR>
     * <BR>
     * <B><U> Actions</U> :</B><BR>
     * <BR>
     * <li>If the attacker/target is dead or use fake death, notify the AI with EVT_CANCEL and send a Server->Client packet ActionFailed (if attacker is a L2PcInstance)</li> <li>If attack isn't aborted, send a message system (critical hit, missed...) to attacker/target if they are L2PcInstance</li>
     * <li>If attack isn't aborted and hit isn't missed, reduce HP of the target and calculate reflection damage to reduce HP of attacker if necessary</li> <li>if attack isn't aborted and hit isn't missed, manage attack or cast break of the target (calculating rate, sending message...)</li><BR>
     * <BR>
     * @param target The L2Character targeted
     * @param damage Nb of HP to reduce
     * @param crit True if hit is critical
     * @param miss True if hit is missed
     * @param soulshot True if SoulShot are charged
     * @param shld True if shield is efficient
     */
    public boolean onHitTimer(ApplyDamagePacket attack, Entity target, int damage, boolean crit, boolean miss, boolean soulshot, byte shld) {
        // If the attacker/target is dead or use fake death, notify the AI with EVT_CANCEL
        // and send a Server->Client packet ActionFailed (if attacker is a L2PcInstance)
        if ((target == null)) {
            getAi().notifyEvent(Event.CANCEL);
            return false;
        }

        if (target.isDead() || (!getKnownList().knowsObject(target))) {
            // getAI().setIntention(CtrlIntention.AI_INTENTION_ACTIVE, null);
            getAi().notifyEvent(Event.CANCEL);

            //TODO Verify action type?
            sendPacket(new ActionFailedPacket(PlayerAction.AutoAttack.getValue()));
            return false;
        }

        if (miss) {
            if (target instanceof PlayerInstance) {
                SystemMessagePacket sm = new SystemMessagePacket(SystemMessageId.AVOIDED_C1_ATTACK);
                if(isPlayer()) {
                    sm.addPcName((PlayerInstance) target);
                } else {
                    sm.addString(((NpcInstance) this).getTemplate().getName());
                }

                sm.writeMe();
                ((PlayerInstance) target).sendPacket(sm);
            }
        }

        // If attack isn't aborted, send a message system (critical hit, missed...) to attacker/target if they are L2PcInstance
        if (!isAttackAborted()) {
            sendDamageMessage(target, damage, false, crit, miss);

            if (attack.hasHits()) {
                attack.writeMe();
                sendPacket(attack);
                broadcastPacket(attack);
            }

            // If L2Character target is a L2PcInstance, send a system message
            if (target instanceof PlayerInstance) {
                PlayerInstance enemy = (PlayerInstance) target;

                // Check if shield is efficient
                if (shld > 0) {
                    enemy.sendPacket(new SystemMessagePacket(SystemMessageId.SHIELD_DEFENCE_SUCCESSFULL));
                    // else if (!miss && damage < 1)
                    // enemy.sendMessage("You hit the target's armor.");
                }
            }

            if (!miss && (damage > 0)) {
                DBWeapon weapon = getActiveWeaponItem();
                boolean isBow = ((weapon != null) && weapon.getType().toString().equalsIgnoreCase("Bow"));

                if (!isBow) {
                    // Do not reflect or absorb if weapon is of type bow
                    // Reduce HP of the target and calculate reflection damage to reduce HP of attacker if necessary
                    double reflectPercent = target.getStat().calcStat(Stats.REFLECT_DAMAGE_PERCENT, 0, null, null);

                    if (reflectPercent > 0) {
                        int reflectedDamage = (int) ((reflectPercent / 100.) * damage);
                        damage -= reflectedDamage;

                        if (reflectedDamage > target.getMaxHp()) {
                            reflectedDamage = target.getMaxHp();
                        }

                        getStatus().reduceHp(reflectedDamage, target, true);
                    }

                    // Absorb HP from the damage inflicted
                    double absorbPercent = getStat().calcStat(Stats.ABSORB_DAMAGE_PERCENT, 0, null, null);

                    if (absorbPercent > 0) {
                        int maxCanAbsorb = (int) (getMaxHp() - getCurrentHp());
                        int absorbDamage = (int) ((absorbPercent / 100.) * damage);

                        if (absorbDamage > maxCanAbsorb) {
                            absorbDamage = maxCanAbsorb; // Can't absord more than max hp
                        }

                        if (absorbDamage > 0) {
                            setCurrentHp(getCurrentHp() + absorbDamage, true);
                        }
                    }
                }

                target.reduceCurrentHp(damage, this, null);

                // Notify AI with EVT_ATTACKED
                if(target.getAi() != null) {
                    target.getAi().notifyEvent(Event.ATTACKED, this);
                }

                // Manage attack or cast break of the target (calculating rate, sending message...)
                if (Formulas.calcAtkBreak(target, damage)) {
                    target.breakAttack();
                    target.breakCast();
                }
            }

            return true;
        }

        getAi().notifyEvent(Event.CANCEL);

        return false;
    }

    public void breakAttack() {
        attackEndTime = -1;
        attackHitTime = -1;
    }

    public void breakCast() {
        castEndTime = -1;
    }

    /**
     * Return True if the L2Character has aborted its attack.<BR>
     * <BR>
     * @return true, if is attack aborted
     */
    public final boolean isAttackAborted() {
        return attackEndTime <= 0;
    }

    /**
     * Add Exp and Sp to the L2Character.<BR>
     * <BR>
     * <B><U> Overriden in </U> :</B><BR>
     * <BR>
     * <li>L2PcInstance</li> <li>L2PetInstance</li><BR>
     * <BR>
     * @param addToExp the add to exp
     * @param addToSp the add to sp
     */
    public void addExpAndSp(long addToExp, int addToSp)
    {
        // Dummy method (overridden by players and pets)
    }

    public boolean isAttacking() {
        return attackEndTime > GameTimeControllerService.getInstance().getGameTicks();
    }

    public boolean isAnimationLocked() {
        return attackHitTime > GameTimeControllerService.getInstance().getGameTicks();
    }

    public boolean isCasting() {
        return castEndTime > GameTimeControllerService.getInstance().getGameTicks();
    }
    public boolean isChanneling() {
        return channelEndTime > GameTimeControllerService.getInstance().getGameTicks();
    }

    public boolean canAttack() {
        return !isAttacking();
    }

    // Return the Attack Speed of the Entity (delay (in milliseconds) before next attack)
    public int calculateTimeBetweenAttacks() {
        float atkSpd = getPAtkSpd();
        return FormulasLegacy.getInstance().calcPAtkSpd(atkSpd);
    }

    // Returns the Attack cooldown
    public int calculateCooldown() {
        //TODO calculate cooldown
        return 0;
    }

    @Override
    public void destroy() {
        super.destroy();

        getAi().clearTarget();
        getKnownList().removeAllKnownObjects();
    }

    @Override
    public EntityKnownList getKnownList() {
        if ((super.getKnownList() == null) || !(super.getKnownList() instanceof EntityKnownList)) {
            setKnownList(new EntityKnownList(this));
        }
        return ((EntityKnownList) super.getKnownList());
    }


    @Override
    public void broadcastPacket(SendablePacket packet) {
        for (PlayerInstance player : getKnownList().getKnownPlayers().values()) {
            sendPacketToPlayer(player, packet);
        }
    }

    public boolean shareCurrentAction(PlayerInstance player) {
        if(getAi() == null) {
            return false;
        }

        sendPacketToPlayer(player, new ObjectPositionPacket(getId(), getPosition().getWorldPosition()));

        // Share current target with player
        if(getAi().getTarget() != null) {
            sendPacketToPlayer(player, new EntitySetTargetPacket(getId(), getAi().getTarget().getId()));
        }
        return true;
    }

    public void sendPacketToPlayer(PlayerInstance player, SendablePacket packet) {
        if(!player.sendPacket(packet)) {
            log.warn("Packet could not be sent to player");
            getKnownList().removeKnownObject(player);
        }
    }

    /**
     * Send system message about damage.
     * @param target
     * @param damage
     * @param mcrit
     * @param pcrit
     * @param miss
     */
    public void sendDamageMessage(Entity target, int damage, boolean mcrit, boolean pcrit, boolean miss) {
        if (miss && target.isPlayer()) {
            SystemMessagePacket sm = SystemMessagePacket.getSystemMessage(SystemMessageId.C1_EVADED_C2_ATTACK);
            sm.addPcName((PlayerInstance) target);
            sm.addCharName(this);
            sm.writeMe();
            target.sendPacket(sm);
        }
    }

    /**
            * Add a Func to the Calculator set of the Entity.<br>
	 * <b><u>Concept</u>:</b> A Entity owns a table of Calculators called <b>_calculators</b>.<br>
	 * Each Calculator (a calculator per state) own a table of Func object.<br>
	 * A Func object is a mathematical function that permit to calculate the modifier of a state (ex : REGENERATE_HP_RATE...).<br>
	 * To reduce cache memory use, L2NPCInstances who don't have skills share the same Calculator set called <b>NPC_STD_CALCULATOR</b>.<br>
            * That's why, if a L2NPCInstance is under a skill/spell effect that modify one of its state, a copy of the NPC_STD_CALCULATOR must be create in its _calculators before adding new Func object.<br>
            * <b><u>Actions</u>:</b>
            * <ul>
	 * <li>If _calculators is linked to NPC_STD_CALCULATOR, create a copy of NPC_STD_CALCULATOR in _calculators</li>
            * <li>Add the Func object to _calculators</li>
            * </ul>
            * @param function The Func object to add to the Calculator corresponding to the state affected
	 */
    private void addStatFunc(AbstractFunction function) {
        if (function == null) {
            return;
        }

        synchronized (this) {
            // Check if Calculator set is linked to the standard Calculator set of NPC
            if (calculators == NPC_STD_CALCULATOR) {
                // Create a copy of the standard NPC Calculator set
                calculators = new Calculator[NUM_STATS];

                for (int i = 0; i < NUM_STATS; i++) {
                    if (NPC_STD_CALCULATOR[i] != null) {
                        calculators[i] = new Calculator(NPC_STD_CALCULATOR[i]);
                    }
                }
            }

            // Select the Calculator of the affected state in the Calculator set
            int stat = function.getStat().ordinal();

            if (calculators[stat] == null) {
                calculators[stat] = new Calculator();
            }

            // Add the Func to the calculator corresponding to the state
            calculators[stat].addFunc(function);
        }
    }

    /**
     * Add a list of Funcs to the Calculator set of the Entity.<br>
     * <B><U>Concept</U>:</B><br>
     * A Entity owns a table of Calculators called <B>_calculators</B>.<br>
     * Each Calculator (a calculator per state) own a table of Func object.<br>
     * A Func object is a mathematic function that permit to calculate the modifier of a state (ex : REGENERATE_HP_RATE...).<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method is ONLY for PlayerInstance</B></FONT><br>
     * <B><U>Example of use</U>:</B>
     * <ul>
     * <li>Equip an item from inventory</li>
     * <li>Learn a new passive skill</li>
     * <li>Use an active skill</li>
     * </ul>
     * @param functions The list of Func objects to add to the Calculator corresponding to the state affected
     */
    public final void addStatFuncs(List<AbstractFunction> functions) {
        if (!(this.isPlayer()) && getKnownList().getKnownPlayers().isEmpty()) {
            for (AbstractFunction f : functions) {
                addStatFunc(f);
            }
        } else {
            final List<Stats> modifiedStats = new ArrayList<>();
            for (AbstractFunction f : functions) {
                modifiedStats.add(f.getStat());
                addStatFunc(f);
            }

            broadcastModifiedStats(modifiedStats);
        }
    }

    public final void addStatFuncs(AbstractFunction function) {
        addStatFuncs(Collections.singletonList(function));
    }

    /**
     * Remove a Func from the Calculator set of the Entity.<br>
     * <B><U>Concept</U>:</B><br>
     * A Entity owns a table of Calculators called <B>_calculators</B>.<br>
     * Each Calculator (a calculator per state) own a table of Func object.<br>
     * A Func object is a mathematic function that permit to calculate the modifier of a state (ex : REGENERATE_HP_RATE...).<br>
     * To reduce cache memory use, L2NPCInstances who don't have skills share the same Calculator set called <B>NPC_STD_CALCULATOR</B>.<br>
     * That's why, if a L2NPCInstance is under a skill/spell effect that modify one of its state, a copy of the NPC_STD_CALCULATOR must be create in its _calculators before addind new Func object.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Remove the Func object from _calculators</li>
     * <li>If Entity is a L2NPCInstance and _calculators is equal to NPC_STD_CALCULATOR, free cache memory and just create a link on NPC_STD_CALCULATOR in _calculators</li>
     * </ul>
     * @param function The Func object to remove from the Calculator corresponding to the state affected
     */
    private final void removeStatFunc(AbstractFunction function) {
        if (function == null) {
            return;
        }

        // Select the Calculator of the affected state in the Calculator set
        int stat = function.getStat().ordinal();

        synchronized (this) {
            if (calculators[stat] == null) {
                return;
            }

            // Remove the Func object from the Calculator
            calculators[stat].removeFunc(function);

            if (calculators[stat].size() == 0) {
                calculators[stat] = null;
            }

            // If possible, free the memory and just create a link on NPC_STD_CALCULATOR
            if (this.isNpc()) {
                int i = 0;
                for (; i < NUM_STATS; i++) {
                    if (!Calculator.equalsCals(calculators[i], NPC_STD_CALCULATOR[i])) {
                        break;
                    }
                }

                if (i >= NUM_STATS) {
                    calculators = NPC_STD_CALCULATOR;
                }
            }
        }
    }

    /**
     * Remove a list of Funcs from the Calculator set of the PlayerInstance.<br>
     * <B><U>Concept</U>:</B><br>
     * A Entity owns a table of Calculators called <B>_calculators</B>.<br>
     * Each Calculator (a calculator per state) own a table of Func object.<br>
     * A Func object is a mathematic function that permit to calculate the modifier of a state (ex : REGENERATE_HP_RATE...).<br>
     * <FONT COLOR=#FF0000><B> <U>Caution</U> : This method is ONLY for PlayerInstance</B></FONT><br>
     * <B><U>Example of use</U>:</B>
     * <ul>
     * <li>Unequip an item from inventory</li>
     * <li>Stop an active skill</li>
     * </ul>
     * @param functions The list of Func objects to add to the Calculator corresponding to the state affected
     */
    public final void removeStatFuncs(AbstractFunction[] functions) {
        if (!(this.isPlayer()) && getKnownList().getKnownPlayers().isEmpty()) {
            for (AbstractFunction f : functions) {
                removeStatFunc(f);
            }
        } else {
            final List<Stats> modifiedStats = new ArrayList<>();
            for (AbstractFunction f : functions) {
                modifiedStats.add(f.getStat());
                removeStatFunc(f);
            }

            broadcastModifiedStats(modifiedStats);
        }
    }

    /**
     * Remove all Func objects with the selected owner from the Calculator set of the Entity.<br>
     * <B><U>Concept</U>:</B><br>
     * A Entity owns a table of Calculators called <B>_calculators</B>.<br>
     * Each Calculator (a calculator per state) own a table of Func object.<br>
     * A Func object is a mathematic function that permit to calculate the modifier of a state (ex : REGENERATE_HP_RATE...).<br>
     * To reduce cache memory use, L2NPCInstances who don't have skills share the same Calculator set called <B>NPC_STD_CALCULATOR</B>.<br>
     * That's why, if a L2NPCInstance is under a skill/spell effect that modify one of its state, a copy of the NPC_STD_CALCULATOR must be create in its _calculators before addind new Func object.<br>
     * <B><U>Actions</U>:</B>
     * <ul>
     * <li>Remove all Func objects of the selected owner from _calculators</li>
     * <li>If Entity is a L2NPCInstance and _calculators is equal to NPC_STD_CALCULATOR, free cache memory and just create a link on NPC_STD_CALCULATOR in _calculators</li>
     * </ul>
     * <B><U>Example of use</U>:</B>
     * <ul>
     * <li>Unequip an item from inventory</li>
     * <li>Stop an active skill</li>
     * </ul>
     * @param owner The Object(Skill, Item...) that has created the effect
     */
    public final void removeStatsOwner(Object owner) {
        List<Stats> modifiedStats = null;
        int i = 0;
        // Go through the Calculator set
        synchronized (this) {
            for (Calculator calc : calculators) {
                if (calc != null) {
                    // Delete all Func objects of the selected owner
                    if (modifiedStats != null) {
                        modifiedStats.addAll(calc.removeOwner(owner));
                    } else {
                        modifiedStats = calc.removeOwner(owner);
                    }

                    if (calc.size() == 0) {
                        calculators[i] = null;
                    }
                }
                i++;
            }

            // If possible, free the memory and just create a link on NPC_STD_CALCULATOR
            if (this.isNpc()) {
                i = 0;
                for (; i < NUM_STATS; i++) {
                    if (!Calculator.equalsCals(calculators[i], NPC_STD_CALCULATOR[i])) {
                        break;
                    }
                }

                if (i >= NUM_STATS) {
                    calculators = NPC_STD_CALCULATOR;
                }
            }

            broadcastModifiedStats(modifiedStats);
        }
    }

    protected void broadcastModifiedStats(List<Stats> stats) {
        System.out.println("BROADCAST MODIFIED STATS!");
        boolean broadcastFull = false;
        boolean otherStats = false;
        StatusUpdatePacket su = null;

        for (Stats stat : stats) {
            if (stat == Stats.POWER_ATTACK_SPEED) {
                if (su == null) {
                    su = new StatusUpdatePacket(this);
                }
                su.addAttribute(StatusUpdatePacket.ATK_SPD, (int) getPAtkSpd());
            } else if (stat == Stats.MAGIC_ATTACK_SPEED) {
                if (su == null) {
                    su = new StatusUpdatePacket(this);
                }
                su.addAttribute(StatusUpdatePacket.CAST_SPD, getMAtkSpd());
            }
            // else if (stat==Stats.MAX_HP) // TODO: self only and add more stats...
            // {
            // if (su == null) su = new StatusUpdate(getObjectId());
            // su.addAttribute(StatusUpdate.MAX_HP, getMaxHp());
            // }
            else if (stat == Stats.MAX_CP) {
                if (this instanceof PlayerInstance) {
                    if (su == null) {
                        su = new StatusUpdatePacket(this);
                    }
                    su.addAttribute(StatusUpdatePacket.MAX_CP, getMaxCp());
                }
            }
            // else if (stat==Stats.MAX_MP)
            // {
            // if (su == null) su = new StatusUpdate(getObjectId());
            // su.addAttribute(StatusUpdate.MAX_MP, getMaxMp());
            // }
            else if (stat == Stats.RUN_SPEED) {
                broadcastFull = true;
            } else {
                otherStats = true;
            }
        }

        if (this instanceof PlayerInstance) {
            if (broadcastFull) {
                ((PlayerInstance) this).updateAndBroadcastStatus(2);
            } else {
                if (otherStats) {
                    ((PlayerInstance) this).updateAndBroadcastStatus(1);
                    if (su != null) {
                        for (PlayerInstance player : getKnownList().getKnownPlayers().values()) {
                            try {
                                player.sendPacket(su);
                            } catch (NullPointerException e) {
                            }
                        }
                    }
                } else if (su != null) {
                    broadcastPacket(su);
                }
            }
        } else if (this instanceof NpcInstance) {
            if (broadcastFull) {
                for (PlayerInstance player : getKnownList().getKnownPlayers().values()) {
                    if (player != null) {
                        player.sendPacket(new NpcInfoPacket((NpcInstance) this));
                    }
                }
            } else if (su != null) {
                broadcastPacket(su);
            }
        } else if (su != null) {
            broadcastPacket(su);
        }
    }

    /**
     * Initializes the CharStat class of the L2Object, is overwritten in classes that require a different CharStat Type.<br>
     * Removes the need for instanceof checks.
     */
    public void initCharStat() {
        stat = new CharStat(this);
    }

    public final float calcStat(Stats stat, float init) {
        return getStat().calcStat(stat, init, null, null);
    }

    // Stat - NEED TO REMOVE ONCE L2CHARSTAT IS COMPLETE
    public final float calcStat(Stats stat, float init, Entity target, Skill skill) {
        return getStat().calcStat(stat, init, target, skill);
    }

    public int getPAccuracy() {
        return getStat().getPAccuracy();
    }

    public int getMagicAccuracy() {
        return getStat().getMAccuracy();
    }

    public final float getAttackSpeedMultiplier() {
        return getStat().getAttackSpeedMultiplier();
    }

    public final float getCriticalDmg(Entity target, float init) {
        return getStat().getCriticalDmg(target, init);
    }

    public int getCriticalHit(Entity target, Skill skill) {
        return getStat().getCriticalHit(target, skill);
    }

    public int getPEvasionRate(Entity target) {
        return getStat().getPEvasionRate(target);
    }

    public int getMagicEvasionRate(Entity target) {
        return getStat().getMEvasionRate(target);
    }

    public final float getMagicalAttackRange(Skill skill) {
        return getStat().getMagicalAttackRange(skill);
    }

    public final int getMaxCp() {
        return getStat().getMaxCp();
    }

    public final int getMaxRecoverableCp() {
        return getStat().getMaxRecoverableCp();
    }

    public float getMAtk(Entity target, Skill skill) {
        return getStat().getMAtk(target, skill);
    }

    public int getMAtkSpd() {
        return getStat().getMAtkSpd();
    }

    public int getMaxMp() {
        return getStat().getMaxMp();
    }

    public int getMaxRecoverableMp() {
        return getStat().getMaxRecoverableMp();
    }

    public int getMaxHp() {
        return getStat().getMaxHp();
    }

    public int getMaxRecoverableHp() {
        return getStat().getMaxRecoverableHp();
    }

    public final int getMCriticalHit(Entity target, Skill skill) {
        return getStat().getMCriticalHit(target, skill);
    }

    public float getMDef(Entity target, Skill skill) {
        return getStat().getMDef(target, skill);
    }

    public float getMReuseRate(Skill skill) {
        return getStat().getMReuseRate(skill);
    }

    public float getPAtk(Entity target) {
        return getStat().getPAtk(target);
    }

    public float getPAtkSpd() {
        return getStat().getPAtkSpd();
    }

    public float getPDef(Entity target) {
        return getStat().getPDef(target);
    }

    public final float getPhysicalAttackRange() {
        return getStat().getPhysicalAttackRange();
    }

    public float getMovementSpeedMultiplier() {
        return getStat().getMovementSpeedMultiplier();
    }

    public float getRunSpeed() {
        return getStat().getRunSpeed();
    }

    public float getWalkSpeed() {
        return getStat().getWalkSpeed();
    }

    public final float getSwimRunSpeed() {
        return getStat().getSwimRunSpeed();
    }

    public final float getSwimWalkSpeed() {
        return getStat().getSwimWalkSpeed();
    }

    public float getMoveSpeed() {
        return getStat().getMoveSpeed();
    }

    public final int getShldDef() {
        return getStat().getShldDef();
    }

    public int getSTR() {
        return getStat().getSTR();
    }

    public int getDEX() {
        return getStat().getDEX();
    }

    public int getCON() {
        return getStat().getCON();
    }

    public int getINT() {
        return getStat().getINT();
    }

    public int getWIT() {
        return getStat().getWIT();
    }

    public int getMEN() {
        return getStat().getMEN();
    }

    // Status - NEED TO REMOVE ONCE L2CHARTATUS IS COMPLETE
    public void addStatusListener(PlayerInstance object) {
        getStatus().addStatusListener(object);
    }

    public int getLevel() {
        return getStat().getLevel();
    }

    public float getLevelMod() {
        return ((getLevel() + 89f) / 100f);
    }

    public boolean isInFrontOfTarget() {
        return false;
    }

    public boolean isBehindTarget() {
        return false;
    }

    public boolean isFacing(Entity attacker, int degreeSide) {
        return true;
    }

    public final float getRandomDamageMultiplier() {
        DBWeapon activeWeapon = getActiveWeaponItem();
        int random;

        if (activeWeapon != null) {
            random = activeWeapon.getRndDmg();
        } else {
            random = 5 + (int) Math.sqrt(getLevel());
        }

        return (1 + ((float) Rnd.get(-random, random) / 100));
    }

    /**
     * Return the active weapon instance (always equiped in the right hand).<BR>
     * <BR>
     * <B><U> Overriden in </U> :</B><BR>
     * <BR>
     * <li>L2PcInstance</li><BR>
     * <BR>
     * @return the active weapon instance
     */
    public abstract ItemInstance getActiveWeaponInstance();

    /**
     * Return the secondary weapon instance (always equiped in the left hand).<BR>
     * <BR>
     * <B><U> Overriden in </U> :</B><BR>
     * <BR>
     * <li>L2PcInstance</li><BR>
     * <BR>
     * @return the secondary weapon instance
     */
    public abstract ItemInstance getSecondaryWeaponInstance();

    public abstract DBWeapon getActiveWeaponItem();
    public abstract DBArmor getSecondaryWeaponItem();

    public void addOverrideCond(PlayerCondOverride... excs) {
        for (PlayerCondOverride exc : excs) {
            exceptions |= exc.getMask();
        }
    }

    public void removeOverridedCond(PlayerCondOverride... excs) {
        for (PlayerCondOverride exc : excs) {
            exceptions &= ~exc.getMask();
        }
    }

    public boolean canOverrideCond(PlayerCondOverride excs) {
        return (exceptions & excs.getMask()) == excs.getMask();
    }

    public void setOverrideCond(long masks) {
        exceptions = masks;
    }

    public float getCurrentHp() {
        return status.getCurrentHp();
    }

    public void setCurrentHpMp(float newHp, float newMp) {
        status.setCurrentHpMp(newHp, newMp);
    }

    public void setCurrentHp(float hp, boolean broadcast) {
        status.setCurrentHp(hp, broadcast);
    }

    public void reduceCurrentHp(float damage, Entity attacker, Skill skill) {
        reduceCurrentHp(damage, attacker, true, false, skill);
    }

    public void reduceCurrentHpByDOT(float damage, Entity attacker, Skill skill) {
        reduceCurrentHp(damage, attacker, !skill.isToggle(), true, skill);
    }

    public void reduceCurrentHp(float damage, Entity attacker, boolean awake, boolean isDOT, Skill skill) {
        getStatus().reduceHp(damage, attacker, awake, isDOT, false);
    }

    /**
     * Reduce current mp.
     * @param i the i
     */
    public void reduceCurrentMp(int i) {
        getStatus().reduceMp(i);
    }

    public float getCurrentMp() {
        return getStatus().getCurrentMp();
    }

    public void setCurrentMp(int mp, boolean broadcast) {
        status.setCurrentMp(mp, broadcast);
    }

    public float getCurrentCp() {
        return 0;
    }

    public void setCurrentCp(int cp, boolean broadcast) {

    }

    public boolean isInvul() {
        return false;
    }

    public boolean isHpBlocked() {
        return false;
    }

    public boolean isStunned() {
        return false;
    }

    public boolean isMortal() {
        return true;
    }

    public void stopStunning(boolean value) {

    }

    public void stopSkillEffects(boolean awake, int effectId) {

    }

    public void stopEffectsOnDamage(boolean awake) {

    }

    public void abortAttack() {
        if (isAttacking()) {
            attackEndTime = -1;
            attackHitTime = -1;
            getAi().notifyEvent(Event.CANCEL);
            sendPacket(new ActionFailedPacket(PlayerAction.AutoAttack.getValue()));
        }
    }

    public void abortCast() {

    }

    public void broadcastStatusUpdate() {
        //TODO broadcast packet
    }

    public boolean isInActiveRegion() {
        WorldRegion region = getWorldRegion();
        return ((region != null));
    }

    public void stopAllTimers() {
        status.stopHpMpRegeneration();
    }

    //layer.isStunned() || player.isSleeping() || player.isParalyzed() || player.isAlikeDead()

    public boolean isSleeping() {
        return false;
    }

    public boolean isParalyzed() {
        return false;
    }

    public boolean isAlikeDead() {
        return false;
    }
}
