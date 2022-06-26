package com.shnok.model.entities;

import com.shnok.GameTimeController;
import com.shnok.Server;
import com.shnok.ai.BaseAI;
import com.shnok.ai.enums.Event;
import com.shnok.model.GameObject;
import com.shnok.model.Point3D;
import com.shnok.model.status.Status;
import com.shnok.pathfinding.Geodata;
import com.shnok.pathfinding.PathFinding;
import com.shnok.pathfinding.node.NodeLoc;
import com.shnok.serverpackets.ObjectMoveTo;
import com.shnok.serverpackets.ObjectPosition;

import java.util.List;

/**
 * This class represents all entities in the world.<BR>
 * <BR>
 * Such as : Players or AIs<BR>
 * <BR>
 */
public abstract class Entity extends GameObject {
    protected boolean _canMove = true;
    protected MoveData _moveData;
    protected BaseAI _ai;

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

        _moveData = new NpcInstance.MoveData();

        /* find path using pathfinder */
        if (_moveData.path == null || _moveData.path.size() == 0) {
            _moveData.path = PathFinding.getInstance().findPath(
                    (int) Math.floor(getPosX()), (int) Math.floor(getPosY()), (int) Math.floor(getPosZ()), x, y, z);
        }

        /* check if path was found */
        if (_moveData.path == null || _moveData.path.size() == 0) {
            return false;
        }

        moveToNextRoutePoint();
        GameTimeController.getInstance().addMovingObject(this);
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

        if (_moveData == null || _moveData.path == null || _moveData.path.size() == 0) {
            return false;
        }

        if (!isOnGeoData()) {
            // Cancel the move action
            _moveData = null;
            return false;
        }

        float x = _moveData.path.get(0).getX() + 0.5f;
        float y = _moveData.path.get(0).getY();
        float z = _moveData.path.get(0).getZ() + 0.5f;
        float distance = calcDistance(getPos(), new Point3D(x, y, z));
        float dx = (x - getPosX());
        float dy = (y - getPosY());
        float dz = (z - getPosZ());
        float xt = dx / distance;
        float yt = dy / distance;
        float zt = dz / distance;

        // calculate the number of ticks between the current position and the destination
        _moveData._ticksToMove = 1 + (int) ((GameTimeController.TICKS_PER_SECOND * distance) / speed);
        // calculate the distance to travel for each tick
        _moveData._xSpeedTicks = (xt * speed) / GameTimeController.TICKS_PER_SECOND;
        _moveData._ySpeedTicks = (yt * speed) / GameTimeController.TICKS_PER_SECOND;
        _moveData._zSpeedTicks = (zt * speed) / GameTimeController.TICKS_PER_SECOND;
        _moveData._xDestination = x;
        _moveData._yDestination = y;
        _moveData._zDestination = z;
        _moveData._moveStartTime = GameTimeController.getGameTicks();
        _moveData.path.remove(0);

        /* send destination to clients */
        ObjectMoveTo packet = new ObjectMoveTo(getId(), new Point3D(x, y, z));
        Server.getInstance().broadcastAll(packet);

        return true;
    }

    public boolean isOnGeoData() {
        return Geodata.getInstance().isInsideBounds(
                (int) Math.floor(getPosX()), (int) Math.floor(getPosY()), (int) Math.floor(getPosZ()));
    }

    public boolean updatePosition(int gameTicks) {
        if (_moveData == null) {
            return true;
        }

        if (_moveData._moveTimestamp == gameTicks) {
            return false;
        }

        // calculate the time since started moving
        int elapsed = gameTicks - _moveData._moveStartTime;

        if (elapsed >= _moveData._ticksToMove) {
            _moveData._moveTimestamp = gameTicks;

            Point3D newPos = new Point3D(_moveData._xDestination, _moveData._yDestination, _moveData._zDestination);
            setPosition(newPos);

            /* share new position with clients */
            ObjectPosition packet = new ObjectPosition(getId(), newPos);
            Server.getInstance().broadcastAll(packet);

            if (_moveData.path.size() > 0) {
                moveToNextRoutePoint();
                return false;
            }

            if (_ai != null) {
                _ai.notifyEvent(Event.ARRIVED);
            }

            return true;
        }

        return false;
    }

    public void attachAI(BaseAI ai) {
        _ai = ai;
    }

    public void detachAI() {
        _ai = null;
    }

    public static class MoveData {
        public int _moveTimestamp;
        public float _xDestination;
        public float _yDestination;
        public float _zDestination;
        public int _moveStartTime;
        public int _ticksToMove;
        public float _xSpeedTicks;
        public float _ySpeedTicks;
        public float _zSpeedTicks;
        public List<NodeLoc> path;
    }
}
