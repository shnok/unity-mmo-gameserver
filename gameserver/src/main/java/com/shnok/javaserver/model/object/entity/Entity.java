package com.shnok.javaserver.model.object.entity;

import com.shnok.javaserver.config.ServerConfig;
import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.dto.serverpackets.EntitySetTargetPacket;
import com.shnok.javaserver.dto.serverpackets.ObjectMoveToPacket;
import com.shnok.javaserver.dto.serverpackets.ObjectPositionPacket;
import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.object.GameObject;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.knownlist.EntityKnownList;
import com.shnok.javaserver.model.skills.Formulas;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.model.template.EntityTemplate;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.MoveData;
import com.shnok.javaserver.pathfinding.PathFinding;
import com.shnok.javaserver.service.GameTimeControllerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.thread.ai.BaseAI;
import com.shnok.javaserver.util.VectorUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Random;

import static com.shnok.javaserver.config.Configuration.serverConfig;

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
    protected boolean canMove = true;
    protected MoveData moveData;
    protected BaseAI ai;
    protected EntityTemplate template;
    protected Status status;
    protected boolean moving;
    protected long attackEndTime;

    public Entity(int id) {
        super(id);
    }

    public void inflictDamage(Entity attacker, int value) {
        if(getAi() != null) {
            getAi().notifyEvent(Event.ATTACKED, attacker);
        }
    }

    public EntityTemplate getTemplate() {
        return template;
    }

    public abstract void setStatus(Status status);

    public abstract boolean canMove();

    public boolean isDead() {
        return getStatus().getHp() <= 0;
    }

    public void onDeath() {
        log.debug("[{}] Entity died", getId());
        if (ai != null) {
            ai.notifyEvent(Event.DEAD);
        }

        setCanMove(false);
        //TODO: stop hp mp regen
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

        ThreadPoolManagerService.getInstance().scheduleAi(new ScheduleNotifyAITask(Event.READY_TO_ACT), timeAtk + cooldown);
    }

    public void doSimpleAttack(Entity target, int timeToHit) {
        //TODO do damage calculations
        int damage = 1;
        boolean criticalHit = false;
        Random r = new Random();
        if(r.nextInt(6) == 0) {
            criticalHit = true;
        }

        if(this instanceof PlayerInstance) {
            damage = 40;
        }

        getAi().clientStartAutoAttack(target);

        log.debug("ouchie?");
        ThreadPoolManagerService.getInstance().scheduleAi(new ScheduleHitTask(target, damage, criticalHit), timeToHit);
    }

    public boolean onHitTimer(Entity target, int damage, boolean criticalHit) {
        log.debug("ouchie!");
        //TODO do apply damage
        //TODO share hit
        //TODO share hp
        //TODO if was walking around remove from moving objects

        if (target == null || target.isDead() || !getKnownList().knowsObject(target)) {
            getAi().notifyEvent(Event.CANCEL);
            return false;
        }

        target.inflictDamage(this, damage);

        return true;
    }

    public boolean isAttacking() {
        return attackEndTime > GameTimeControllerService.getInstance().getGameTicks();
    }

    public boolean canAttack() {
        return !isAttacking();
    }

    // Return the Attack Speed of the L2Character (delay (in milliseconds) before next attack)
    public int calculateTimeBetweenAttacks() {
        float atkSpd = getTemplate().getBasePAtkSpd();
        return Formulas.getInstance().calcPAtkSpd(atkSpd);
    }

    // Returns the Attack cooldown
    public int calculateCooldown() {
        //TODO calculate cooldown
        return 0;
    }

    @Override
    public void destroy() {
        super.destroy();
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
        if(distanceDelta >= serverConfig.geodataNodeSize() * 2) {
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
            if(serverConfig.geodataPathFinderEnabled()) {

                moveData.path = PathFinding.getInstance().findPath(getPosition().getWorldPosition(), destination, stopAtRange);
                if(serverConfig.printPathfinder()) {
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

        if(serverConfig.printPathfinder()) {
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
        int speed;

        if(!canMove() || getAi() == null) {
            return false;
        }

        // safety
        if (moveData == null || moveData.path == null || moveData.path.size() == 0) {
            return false;
        }

        //TODO add speed calculations, move switch between speeds into AI
        if(getAi().getMovingReason() == EntityMovingReason.Walking) {
            speed = getTemplate().getBaseWalkSpd();
        } else {
            speed = getTemplate().getBaseRunSpd();
        }

        getStatus().setMoveSpeed(speed);
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
        updateMoveData(getStatus().getMoveSpeed() / 52.5f);

        GameTimeControllerService.getInstance().addMovingObject(this);

        // calculate heading
        getPosition().setHeading(VectorUtils.calculateMoveDirectionAngle(getPos(), moveData.destination));

        // send destination to known players
        ObjectMoveToPacket packet = new ObjectMoveToPacket(
                getId(),
                new Point3D(moveData.destination),
                getStatus().getMoveSpeed(),
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

            if(serverConfig.printPathfinder()) {
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

    public void broadcastPacket(ServerPacket packet) {
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

    public void sendPacketToPlayer(PlayerInstance player, ServerPacket packet) {
        if(!player.sendPacket(packet)) {
            log.warn("Packet could not be sent to player");
            getKnownList().removeKnownObject(player);
        }
    }

    // Task to destroy object based on delay
    protected static class ScheduleDestroyTask implements Runnable {
        private final Entity entity;

        public ScheduleDestroyTask(Entity entity){
            this.entity = entity;
        }

        @Override
        public void run() {
            log.debug("Execute schedule destroy object");
            if (entity != null) {
                entity.destroy();
            }
        }
    }

    // Task to apply damage based on delay
    private class ScheduleHitTask implements Runnable {
        private final Entity hitTarget;
        private final int damage;
        private final boolean criticalHit;

        public ScheduleHitTask(Entity hitTarget, int damage, boolean criticalHit) {
            this.hitTarget = hitTarget;
            this.damage = damage;
            this.criticalHit = criticalHit;
        }

        @Override
        public void run() {
            try {
                onHitTimer(hitTarget, damage, criticalHit);
            } catch (Throwable e) {
                log.error(e);
            }
        }
    }

    // Task to notify AI based on delay
    public class ScheduleNotifyAITask implements Runnable {

        private final Event event;

        public ScheduleNotifyAITask(Event event) {
            this.event = event;
        }

        @Override
        public void run() {
            try {
                getAi().notifyEvent(event, null);
            } catch (Throwable t) {
                log.warn(t);
            }
        }
    }
}
