package com.shnok.javaserver.model.object.entity;

import com.shnok.javaserver.db.entity.DBArmor;
import com.shnok.javaserver.db.entity.DBWeapon;
import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.dto.external.serverpackets.*;
import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.enums.PlayerCondOverride;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.model.WorldRegion;
import com.shnok.javaserver.model.object.GameObject;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.knownlist.EntityKnownList;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.skills.FormulasLegacy;
import com.shnok.javaserver.model.skills.Skill;
import com.shnok.javaserver.model.stats.CharStat;
import com.shnok.javaserver.model.stats.Formulas;
import com.shnok.javaserver.model.stats.Stats;
import com.shnok.javaserver.model.stats.Calculator;
import com.shnok.javaserver.model.stats.functions.AbstractFunction;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.model.template.EntityTemplate;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.MoveData;
import com.shnok.javaserver.pathfinding.PathFinding;
import com.shnok.javaserver.security.Rnd;
import com.shnok.javaserver.service.GameTimeControllerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.thread.ai.BaseAI;
import com.shnok.javaserver.thread.entity.ScheduleHitTask;
import com.shnok.javaserver.thread.entity.ScheduleNotifyAITask;
import com.shnok.javaserver.util.VectorUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.*;

import static com.shnok.javaserver.config.Configuration.server;
import static com.shnok.javaserver.model.stats.Stats.NUM_STATS;

/**
 * This class represents all entities in the world.<BR>
 * <BR>
 * Such as : Players or AIs<BR>
 * <BR>
 */

@NoArgsConstructor
@Log4j2
@Getter
@Setter
public abstract class Entity extends GameObject {
    /** Table of Calculators containing all used calculator */
    private Calculator[] calculators;

    /** Table of calculators containing all standard NPC calculator (ex : ACCURACY_COMBAT, EVASION_RATE) */
    private static final Calculator[] NPC_STD_CALCULATOR = Formulas.getStdNPCCalculators();
    private CharStat stat;
    protected long exceptions = 0L;
    protected MoveData moveData;
    protected BaseAI ai;
    protected EntityTemplate template;
    protected Status status;
    protected boolean canMove = true;
    protected boolean moving;
    protected boolean running;
    protected long attackEndTime;
    protected long castEndTime;
    protected long channelEndTime;
    protected boolean dead;

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

//    public void inflictDamage(Entity attacker, int value) {
//        if(getAi() != null) {
//            getAi().notifyEvent(Event.ATTACKED, attacker);
//        }
//    }

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

    public abstract boolean canMove();

    public void doDie(Entity attacker) {
        abortCast();
        stopAllTimers();

        setCanMove(false);
        setDead(true);

        log.debug("[{}] Entity died", getId());
        if (ai != null) {
            ai.notifyEvent(Event.DEAD);
        }

        //TODO: give exp?
        //TODO: Share HP
    }

    public void doAttack(Entity target) {
        // Extra target verification at each loop
        if (target == null || target.isDead() || !getKnownList().knowsObject(target)
                || getAi().getIntention() != Intention.INTENTION_ATTACK) {
            getAi().notifyEvent(Event.CANCEL);
            return;
        }

        // Check if attack animation is finished
        if (!canAttack()) {
            return;
        }

        // Get the Attack Speed of the npc (delay (in milliseconds) before next attack)
        int timeAtk = calculateTimeBetweenAttacks();
        log.debug("AtkSpd: {} Attack duration: {}ms", getTemplate().getBasePAtkSpd(), timeAtk);
        // the hit is calculated to happen halfway to the animation
        int timeToHit = timeAtk / 2;

        int cooldown = calculateCooldown();

        attackEndTime = GameTimeControllerService.getInstance().getGameTicks();
        attackEndTime += (timeAtk / GameTimeControllerService.getInstance().getTickDurationMs());
        attackEndTime -= 1;

        // Start hit task
        doSimpleAttack(target, timeToHit);

        ThreadPoolManagerService.getInstance().scheduleAi(new ScheduleNotifyAITask(getAi(), Event.READY_TO_ACT), timeAtk + cooldown);
    }

    public void doSimpleAttack(Entity target, int timeToHit) {
        //TODO do damage calculations
        int damage = 1;
        boolean crit = false;
        Random r = new Random();
        if(r.nextInt(6) == 0) {
            crit = true;
        }

        boolean miss = false;
        boolean soulshot = false;
        boolean shield = false;

        if(this.isPlayer()) {
            damage = 10;
        }

        getAi().clientStartAutoAttack();

        log.debug("ouchie?");
        ThreadPoolManagerService.getInstance().scheduleAi(new ScheduleHitTask(this, target, damage, crit, miss, soulshot, shield), timeToHit);
    }

//    public boolean onHitTimer(Entity target, int damage, boolean criticalHit) {
//        log.debug("ouchie!");
//        //TODO do apply damage
//        //TODO share hit
//        //TODO share hp
//        //TODO if was walking around remove from moving objects
//
//        if (target == null || target.isDead() || !getKnownList().knowsObject(target)) {
//            getAi().notifyEvent(Event.CANCEL);
//            return false;
//        }
//
//        target.getAi().notifyEvent(Event.ATTACKED, this);
//
//        //TODO add notifyDamageReceived
//        target.reduceCurrentHp(damage, this);
//
//        return true;
//    }

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
    public boolean onHitTimer(Entity target, int damage, boolean crit, boolean miss, boolean soulshot, boolean shld) {
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
            sendPacket(new ActionFailedPacket((byte) 0));
            return false;
        }

        if (miss) {
            if (target instanceof PlayerInstance) {
                SystemMessagePacket sm = new SystemMessagePacket(SystemMessageId.AVOIDED_C1_ATTACK);
                sm.addPcName((PlayerInstance) target);
                sm.writeMe();
                ((PlayerInstance) target).sendPacket(sm);
            }
        }

        // If attack isn't aborted, send a message system (critical hit, missed...) to attacker/target if they are L2PcInstance
        if (!isAttackAborted()) {
            sendDamageMessage(target, damage, false, crit, miss);

            // If L2Character target is a L2PcInstance, send a system message
            if (target instanceof PlayerInstance) {
                PlayerInstance enemy = (PlayerInstance) target;

                // Check if shield is efficient
                if (shld) {
                    enemy.sendPacket(new SystemMessagePacket(SystemMessageId.SHIELD_DEFENCE_SUCCESSFULL));
                    // else if (!miss && damage < 1)
                    // enemy.sendMessage("You hit the target's armor.");
                }
            }

            if (!miss && (damage > 0)) {
                DBWeapon weapon = getActiveWeaponItem();
                boolean isBow = ((weapon != null) && weapon.getType().toString().equalsIgnoreCase("Bow"));

                if (!isBow) { // Do not reflect or absorb if weapon is of type bow
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

                target.reduceCurrentHp(damage, this);

                // Notify AI with EVT_ATTACKED
                target.getAi().notifyEvent(Event.ATTACKED, this);

                getAi().clientStartAutoAttack();

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
        float atkSpd = getTemplate().getBasePAtkSpd();
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
    public void setPosition(Point3D position) {
        super.setPosition(position);

        // Update known list
        float distanceDelta = VectorUtils.calcDistance(
                getPosition().getWorldPosition(), getPosition().getLastWorldPosition());


        // Share position after delta is higher than 2 nodes
        if(distanceDelta >= server.geodataNodeSize() * 2) {
            // Share new position to known list
            broadcastPacket(new ObjectPositionPacket(getId(), position));
            getKnownList().forceRecheckSurroundings();
            getPosition().setLastWorldPosition(getPosition().getWorldPosition());
        }
    }

    public boolean moveTo(Point3D destination) {
        return moveTo(destination, 0);
    }

    public boolean moveTo(Point3D destination, float stopAtRange) {
        //System.out.println("AI find path: " + x + "," + y + "," + z);
        if (!isOnGeoData()) {
            log.debug("[{}] Not on geodata", getId());
            return false;
        }

        if(!canMove) {
            return false;
        }

        moveData = new MoveData();

        /* find path using pathfinder */
        if (moveData.path == null || moveData.path.size() == 0) {
            if(server.geodataPathFinderEnabled()) {

                moveData.path = PathFinding.getInstance().findPath(getPosition().getWorldPosition(), destination, stopAtRange);
                if(server.printPathfinder()) {
                    log.debug("[{}] Found path length: {}", getId(), moveData.path.size());
                }
            } else {
                moveData.path =  new ArrayList<>();
                moveData.path.add(new Point3D(destination));
            }
        }

        /* check if path was found */
        if (moveData.path == null || moveData.path.size() == 0) {
            return false;
        }

        if(server.printPathfinder()) {
            log.debug("[{}] Move to {} reason {}", getId(),destination, getAi().getMovingReason());
        }

        if(moveToNextRoutePoint()) {
            moving = true;
            return true;
        }

        return false;
    }

    // calculate how many ticks do we need to move to destination
    public boolean moveToNextRoutePoint() {
        float speed;

        if(!canMove() || getAi() == null) {
            return false;
        }

        // safety
        if (moveData == null || moveData.path == null || moveData.path.size() == 0) {
            return false;
        }

        //TODO add speed calculations, move switch between speeds into AI
        speed = getStat().getMoveSpeed();
        if (speed <= 0) {
            return false;
        }

        /* cancel the move action if not on geodata */
        if (!isOnGeoData()) {
            //TODO run straight to target
            moveData = null;
            return false;
        }

        // calculate how many ticks do we need to move to destination
        updateMoveData(speed / 52.5f);

        GameTimeControllerService.getInstance().addMovingObject(this);

        // calculate heading
        getPosition().setHeading(VectorUtils.calculateMoveDirectionAngle(getPos(), moveData.destination));

        // send destination to known players
        ObjectMoveToPacket packet = new ObjectMoveToPacket(
                getId(),
                new Point3D(moveData.destination),
                getStat().getMoveSpeed(),
                getAi().getMovingReason() == EntityMovingReason.Walking);
        broadcastPacket(packet);

        return true;
    }

    // calculate how many ticks do we need to move to destination
    private void updateMoveData(float moveSpeed) {
        Point3D destination = new Point3D(moveData.path.get(0));
        float distance = VectorUtils.calcDistance2D(getPos(), destination); // Ensure this is 2D distance
        Point3D delta = new Point3D(destination.getX() - getPosX(),
                destination.getY() - getPosY(),
                destination.getZ() - getPosZ());
        Point3D deltaT = new Point3D(delta.getX() / distance,
                delta.getY() / distance,
                delta.getZ() / distance);

        float ticksPerSecond = GameTimeControllerService.getInstance().getTicksPerSecond();
        // calculate the number of ticks between the current position and the destination
        moveData.ticksToMove = 1 + (int) ((ticksPerSecond * distance) / moveSpeed);

        // calculate the distance to travel for each tick
        moveData.xSpeedTicks = (deltaT.getX() * moveSpeed) / ticksPerSecond;
        moveData.ySpeedTicks = (deltaT.getY() * moveSpeed) / ticksPerSecond;
        moveData.zSpeedTicks = (deltaT.getZ() * moveSpeed) / ticksPerSecond;

        moveData.startPosition = new Point3D(getPos());
        moveData.destination = destination;
        moveData.moveStartTime = GameTimeControllerService.getInstance().getGameTicks();
        moveData.path.remove(0);
    }

    // Update entity position based on server ticks and move data
    public boolean updatePosition(long gameTicks) {
        if (moveData == null) {
            return true;  // No movement if moveData is null
        }

        if (moveData.moveTimestamp == gameTicks) {
            return false; // Prevent multiple updates in the same tick
        }

        // Calculate the time elapsed since starting the move
        long elapsed = gameTicks - moveData.moveStartTime;

        // Linear interpolate entity position between start and destination
        Point3D lerpPosition = VectorUtils.lerpPosition(
                moveData.startPosition,
                moveData.destination,
                (float) elapsed / moveData.ticksToMove);
        setPosition(lerpPosition);

        // Check if entity has target and is in range of attack
        if(getAi().getAttackTarget() != null) {
            // Check on a 2D plane to avoid offsets caused by geodata nodes Y
            float distanceToTarget = VectorUtils.calcDistance2D(getAi().getAttackTarget().getPos(), getPos());
            log.debug("Updating position... Distance to target: {}", distanceToTarget);

            if(distanceToTarget <= getTemplate().getBaseAtkRange()) {
                if (ai != null) {
                    ai.notifyEvent(Event.ARRIVED);
                }

                return true;
            }
        }

        // Move to next path node
        if (elapsed >= moveData.ticksToMove) {
            moveData.moveTimestamp = gameTicks;

            if (moveData.path.size() > 0) {
                moveToNextRoutePoint();
                return false;
            }

            if (ai != null) {
                ai.notifyEvent(Event.ARRIVED);
            }

            if(server.printPathfinder()) {
                log.debug("[{}] Reached move data destination.", getId());
            }

            return true;
        }

        return false;
    }

    public boolean isOnGeoData() {
        try {
            Geodata.getInstance().getNodeAt(getPos());
            return true;
        } catch (Exception e) {
//            log.debug("[{}] Not at a valid position: {}", getId(), getPos());
            return false;
        }
    }

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
        if ((stats == null) || stats.isEmpty()) {
            return;
        }
//
//        if (isSummon()) {
//            L2Summon summon = (L2Summon) this;
//            if (summon.getOwner() != null) {
//                summon.updateAndBroadcastStatus(1);
//            }
//        } else {
//            boolean broadcastFull = false;
//            StatusUpdate su = new StatusUpdate(this);
//
//            for (Stats stat : stats) {
//                if (stat == Stats.POWER_ATTACK_SPEED) {
//                    su.addAttribute(StatusUpdate.ATK_SPD, (int) getPAtkSpd());
//                } else if (stat == Stats.MAGIC_ATTACK_SPEED) {
//                    su.addAttribute(StatusUpdate.CAST_SPD, getMAtkSpd());
//                } else if (stat == Stats.MOVE_SPEED) {
//                    broadcastFull = true;
//                }
//            }
//
//            if (isPlayer()) {
//                if (broadcastFull) {
//                    getActingPlayer().updateAndBroadcastStatus(2);
//                } else {
//                    getActingPlayer().updateAndBroadcastStatus(1);
//                    if (su.hasAttributes()) {
//                        broadcastPacket(su);
//                    }
//                }
//                if ((getSummon() != null) && isAffected(EffectFlag.SERVITOR_SHARE)) {
//                    getSummon().broadcastStatusUpdate();
//                }
//            } else if (isNpc()) {
//                if (broadcastFull) {
//                    Collection<PlayerInstance> plrs = getKnownList().getKnownPlayers().values();
//                    for (PlayerInstance player : plrs) {
//                        if ((player == null) || !isVisibleFor(player)) {
//                            continue;
//                        }
//                        if (getRunSpeed() == 0) {
//                            player.sendPacket(new ServerObjectInfo((L2Npc) this, player));
//                        } else {
//                            player.sendPacket(new AbstractNpcInfo.NpcInfo((L2Npc) this, player));
//                        }
//                    }
//                } else if (su.hasAttributes()) {
//                    broadcastPacket(su);
//                }
//            } else if (su.hasAttributes()) {
//                broadcastPacket(su);
//            }
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

    /**
     * Reduce current hp.
     * @param i the i
     * @param attacker the attacker
     */
    public void reduceCurrentHp(int i, Entity attacker) {
        reduceCurrentHp(i, attacker, true);
    }

    /**
     * Reduce current hp.
     * @param i the i
     * @param attacker the attacker
     * @param awake the awake
     */
    public void reduceCurrentHp(int i, Entity attacker, boolean awake) {
        getStatus().reduceHp(i, attacker, awake);
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

    /**
     * Not Implemented.<BR>
     */
    public boolean sendPacket(SendablePacket packet) {
        // default implementation
        return true;
    }
}
