package com.shnok.javaserver.model.object;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.dto.external.serverpackets.ObjectMoveToPacket;
import com.shnok.javaserver.dto.external.serverpackets.ObjectPositionPacket;
import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.stats.CharStat;
import com.shnok.javaserver.model.template.EntityTemplate;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.MoveData;
import com.shnok.javaserver.pathfinding.PathFinding;
import com.shnok.javaserver.service.GameTimeControllerService;
import com.shnok.javaserver.thread.ai.BaseAI;
import com.shnok.javaserver.util.VectorUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
@Getter
@Setter
public abstract class MovableObject extends GameObject {
    protected BaseAI ai;
    protected MoveData moveData;
    protected boolean canMove = true;
    protected boolean moving;
    protected boolean running;
    protected CharStat stat;
    protected EntityTemplate template;

    public MovableObject(int id) {
        super(id);
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

    public abstract boolean canMove();

    /**
     * Not Implemented.<BR>
     */
    public abstract boolean sendPacket(SendablePacket packet);

    /**
     * Not Implemented.<BR>
     */
    public abstract void broadcastPacket(SendablePacket packet);
}
