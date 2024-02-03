package com.shnok.javaserver.model.entity;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.dto.serverpackets.ObjectAnimationPacket;
import com.shnok.javaserver.dto.serverpackets.ObjectMoveToPacket;
import com.shnok.javaserver.dto.serverpackets.ObjectPositionPacket;
import com.shnok.javaserver.enums.EntityAnimation;
import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.model.GameObject;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.knownlist.EntityKnownList;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.model.template.EntityTemplate;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.MoveData;
import com.shnok.javaserver.pathfinding.PathFinding;
import com.shnok.javaserver.service.GameTimeControllerService;
import com.shnok.javaserver.thread.ai.BaseAI;
import com.shnok.javaserver.util.VectorUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;

/**
 * This class represents all entities in the world.<BR>
 * <BR>
 * Such as : Players or AIs<BR>
 * <BR>
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@Log4j2
public abstract class Entity extends GameObject {
    protected boolean canMove = true;
    protected MoveData moveData;
    protected BaseAI ai;
    protected EntityTemplate template;
    protected Status status;

    public Entity(int id) {
        super(id);
    }

    public abstract void inflictDamage(int value);
    public abstract void setStatus(Status status);
    public abstract boolean canMove();
    public abstract void onDeath();

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
        if(distanceDelta > 4.0f) {
            getKnownList().forceRecheckSurroundings();
            getPosition().setLastWorldPosition(getPosition().getWorldPosition());
        }
    }

    public boolean moveTo(Point3D destination) {
        //System.out.println("AI find path: " + x + "," + y + "," + z);
        if (!isOnGeoData()) {
            log.debug("[{}] Not on geodata", getId());
            return false;
        }

        moveData = new MoveData();

        /* find path using pathfinder */
        if (moveData.path == null || moveData.path.size() == 0) {
            if(Config.PATHFINDER_ENABLED) {

                moveData.path = PathFinding.getInstance().findPath(getPosition().getWorldPosition(), destination);
                if(Config.PRINT_PATHFINDER) {
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

        if(Config.PRINT_PATHFINDER) {
            log.debug("[{}] Move to {} reason {}", getId(),destination, getAi().getMovingReason());
        }

        moveToNextRoutePoint();
        GameTimeControllerService.getInstance().addMovingObject(this);
        return true;
    }

    /* calculate how many ticks do we need to move to destination */
    public boolean moveToNextRoutePoint() {
        float speed;

        if(!canMove() || getAi() == null) {
            return false;
        }

        /* safety */
        if (moveData == null || moveData.path == null || moveData.path.size() == 0) {
            return false;
        }

        if(getAi().getMovingReason() == EntityMovingReason.Walking) {
            speed = getTemplate().getBaseWalkSpd();
        } else {
            speed = getTemplate().getBaseRunSpd();
        }

        if (speed <= 0) {
            return false;
        }

        /* cancel the move action if not on geodata */
        if (!isOnGeoData()) {
            moveData = null;
            return false;
        }

        updateMoveData(speed);

        Point3D destination = new Point3D(moveData.destination);

        /* send destination to known players */
        ObjectMoveToPacket packet = new ObjectMoveToPacket(getId(), destination, getStatus().getMoveSpeed());
        broadcastPacket(packet);

        //log.debug("Moving to new point: " + destination);

        /* Set server side position to destination for players loading npc during travel */
        //setPosition(destination);

        return true;
    }

    private void updateMoveData(float moveSpeed) {
        Point3D destination = new Point3D(moveData.path.get(0));
        float distance = VectorUtils.calcDistance(getPos(), destination);
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

    public boolean isOnGeoData() {
        try {
            Geodata.getInstance().getNodeAt(getPos());
            return true;
        } catch (Exception e) {
//            log.debug("[{}] Not at a valid position: {}", getId(), getPos());
            return false;
        }
    }

    public boolean updatePosition(long gameTicks) {
        if (moveData == null) {
            return true;
        }

        if (moveData.moveTimestamp == gameTicks) {
            return false;
        }

        // calculate the time since started moving
        long elapsed = gameTicks - moveData.moveStartTime;

        // lerp entity position between the start position and destination based on server ticks elapsed
        Point3D lerpPosition = VectorUtils.lerpPosition(
                moveData.startPosition,
                moveData.destination,
                (float) elapsed / moveData.ticksToMove);
        setPosition(lerpPosition);

//        log.debug("{} {} {} {}",
//                moveData.startPosition,
//                moveData.destination,
//                (float) elapsed / moveData.ticksToMove,
//                lerpPosition);

        if (elapsed >= moveData.ticksToMove) {
            moveData.moveTimestamp = gameTicks;

            /* share new position with known players */
            ObjectPositionPacket packet = new ObjectPositionPacket(getId(), getPos());
            broadcastPacket(packet);

            if (moveData.path.size() > 0) {
                moveToNextRoutePoint();
                return false;
            }

            if (ai != null) {
                ai.notifyEvent(Event.ARRIVED);
            }

            return true;
        }

        return false;
    }

    public void broadcastPacket(ServerPacket packet) {
//        log.debug("[{}] Known players:{} entities:{} objects:{}",
//                getId(),
//                getKnownList().getKnownPlayers().size(),
//                getKnownList().getKnownCharacters().size(),
//                getKnownList().getKnownObjects().size());
        for (PlayerInstance player : getKnownList().getKnownPlayers().values()) {
            player.sendPacket(packet);
        }
    }

    public void shareCurrentAction(PlayerInstance player) {
        if(getAi() == null) {
            return;
        }

        switch (getAi().getIntention()) {
            case INTENTION_MOVE_TO:
                player.sendPacket(new ObjectMoveToPacket(getId(), moveData.destination, getStatus().getMoveSpeed()));

                if(getAi().getMovingReason() == EntityMovingReason.Walking) {
                    player.sendPacket(new ObjectAnimationPacket(
                            getId(), EntityAnimation.Walk.getValue(), 1f));
                } else if(getAi().getMovingReason() == EntityMovingReason.Running) {
                    player.sendPacket(new ObjectAnimationPacket(
                            getId(), EntityAnimation.Walk.getValue(), 1f));
                }
                break;
            case INTENTION_IDLE:
            case INTENTION_WAITING:
        }
    }
}
