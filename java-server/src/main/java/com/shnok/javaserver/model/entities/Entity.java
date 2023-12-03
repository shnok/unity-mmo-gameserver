package com.shnok.javaserver.model.entities;

import com.shnok.javaserver.service.GameTimeControllerService;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.thread.ai.BaseAI;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.model.GameObject;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.PathFinding;
import com.shnok.javaserver.pathfinding.node.NodeLoc;
import com.shnok.javaserver.dto.serverpackets.ObjectMoveToPacket;
import com.shnok.javaserver.dto.serverpackets.ObjectPositionPacket;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * This class represents all entities in the world.<BR>
 * <BR>
 * Such as : Players or AIs<BR>
 * <BR>
 */

public abstract class Entity extends GameObject {

    @Autowired
    protected ServerService serverService;
    @Autowired
    protected GameTimeControllerService gameTimeControllerService;

    protected boolean canMove = true;
    protected MoveData moveData;
    protected BaseAI ai;

    public Entity() {
    }

    public Entity(int id) {
        super(id);
    }

    public abstract void inflictDamage(int value);

    public abstract Status getStatus();

    public abstract void setStatus(Status status);

    public abstract boolean canMove();

    public abstract void onDeath();

    public boolean moveTo(int x, int y, int z) {
        //System.out.println("AI find path: " + x + "," + y + "," + z);
        if (!isOnGeoData()) {
            return false;
        }

        moveData = new NpcInstance.MoveData();

        /* find path using pathfinder */
        if (moveData.path == null || moveData.path.size() == 0) {
            moveData.path = PathFinding.getInstance().findPath(
                    (int) Math.floor(getPosX()), (int) Math.floor(getPosY()), (int) Math.floor(getPosZ()), x, y, z);
        }

        /* check if path was found */
        if (moveData.path == null || moveData.path.size() == 0) {
            return false;
        }

        moveToNextRoutePoint();
        gameTimeControllerService.addMovingObject(this);
        return true;
    }

    private float calcDistance(Point3D from, Point3D to) {
        double dx = (to.getX() - from.getX());
        double dy = (to.getY() - from.getY());
        double dz = (to.getZ() - from.getZ());
        return (float) Math.sqrt((dx * dx) + (dz * dz) + (dy * dy));
    }

    /* calculate how many ticks do we need to move to destination */
    public boolean moveToNextRoutePoint() {
        float speed = getStatus().getMoveSpeed();
        if ((speed <= 0) || !canMove()) {
            return false;
        }

        if (moveData == null || moveData.path == null || moveData.path.size() == 0) {
            return false;
        }

        if (!isOnGeoData()) {
            // Cancel the move action
            moveData = null;
            return false;
        }

        float x = moveData.path.get(0).getX() + 0.5f;
        float y = moveData.path.get(0).getY();
        float z = moveData.path.get(0).getZ() + 0.5f;
        float distance = calcDistance(getPos(), new Point3D(x, y, z));
        float dx = (x - getPosX());
        float dy = (y - getPosY());
        float dz = (z - getPosZ());
        float xt = dx / distance;
        float yt = dy / distance;
        float zt = dz / distance;

        // calculate the number of ticks between the current position and the destination
        moveData.ticksToMove = 1 + (int) ((GameTimeControllerService.TICKS_PER_SECOND * distance) / speed);
        // calculate the distance to travel for each tick
        moveData.xSpeedTicks = (xt * speed) / GameTimeControllerService.TICKS_PER_SECOND;
        moveData.ySpeedTicks = (yt * speed) / GameTimeControllerService.TICKS_PER_SECOND;
        moveData.zSpeedTicks = (zt * speed) / GameTimeControllerService.TICKS_PER_SECOND;
        moveData.xDestination = x;
        moveData.yDestination = y;
        moveData.zDestination = z;
        moveData.moveStartTime = GameTimeControllerService.getGameTicks();
        moveData.path.remove(0);

        /* send destination to clients */
        ObjectMoveToPacket packet = new ObjectMoveToPacket(getId(), new Point3D(x, y, z));
        serverService.broadcastAll(packet);

        Point3D newPos = new Point3D(moveData.xDestination, moveData.yDestination, moveData.zDestination);
        setPosition(newPos);

        return true;
    }

    public boolean isOnGeoData() {
        return Geodata.getInstance().isInsideBounds(
                (int) Math.floor(getPosX()), (int) Math.floor(getPosY()), (int) Math.floor(getPosZ()));
    }

    public boolean updatePosition(int gameTicks) {
        if (moveData == null) {
            return true;
        }

        if (moveData.moveTimestamp == gameTicks) {
            return false;
        }

        // calculate the time since started moving
        int elapsed = gameTicks - moveData.moveStartTime;

        if (elapsed >= moveData.ticksToMove) {
            moveData.moveTimestamp = gameTicks;

            //Point3D newPos = new Point3D(moveData.xDestination, moveData.yDestination, moveData.zDestination);
            //setPosition(newPos);

            /* share new position with clients */
            ObjectPositionPacket packet = new ObjectPositionPacket(getId(), getPos());
            serverService.broadcastAll(packet);

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

    public void attachAI(BaseAI ai) {
        ai = ai;
    }

    public void detachAI() {
        ai = null;
    }

    public static class MoveData {
        public int moveTimestamp;
        public float xDestination;
        public float yDestination;
        public float zDestination;
        public int moveStartTime;
        public int ticksToMove;
        public float xSpeedTicks;
        public float ySpeedTicks;
        public float zSpeedTicks;
        public List<NodeLoc> path;
    }
}
