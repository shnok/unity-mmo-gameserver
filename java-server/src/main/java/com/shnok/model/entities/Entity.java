package com.shnok.model.entities;

import com.shnok.GameTimeController;
import com.shnok.ai.BaseAI;
import com.shnok.ai.enums.Event;
import com.shnok.model.GameObject;
import com.shnok.model.Point3D;
import com.shnok.model.status.Status;
import com.shnok.pathfinding.PathFinding;
import com.shnok.pathfinding.node.NodeLoc;

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

    public Entity(int id) {
        super(id);
    }

    public abstract void inflictDamage(int value);

    public abstract Status getStatus();

    public abstract void setStatus(Status status);

    public abstract boolean canMove();

    public abstract void onDeath();

    public boolean moveTo(int x, int y, int z) {
        System.out.println("AI find path: " + x + "," + y + "," + z);

        if (_moveData == null) {
            _moveData = new NpcInstance.MoveData();
        }

        if (_moveData.path == null || _moveData.path.size() == 0) {
            _moveData.path = PathFinding.getInstance().findPath((int) getPosX(), (int) getPosY(), (int) getPosZ(), x, y, z);
        }

        if (_moveData.path != null && _moveData.path.size() > 0) {
            followPath();
            GameTimeController.getInstance().addMovingObject(this);
            return true;
        }

        return false;
    }

    private void followPath() {
        float speed = getStatus().getMoveSpeed();
        if ((speed <= 0) || !canMove()) {
            return;
        }

        final float curX = getPosX();
        final float curY = getPosY();
        final float curZ = getPosZ();

        double x = _moveData.path.get(0).getX();
        double y = _moveData.path.get(0).getY();
        double z = _moveData.path.get(0).getZ();
        _moveData._xDestination = (int) x;
        _moveData._yDestination = (int) y;
        _moveData._zDestination = (int) z;

        double dx = (x - curX);
        double dy = (y - curY);
        double dz = (z - curZ);
        double distance = Math.sqrt((dx * dx) + (dz * dz));
        double cos = dx / distance;
        double sin = dz / distance;
        int heading = (int) (Math.atan2(-sin, -cos) * 10430.378350470452724949566316381);
        heading += 32768;


        // Caclulate the Nb of ticks between the current position and the destination
        _moveData._ticksToMove = 1 + (int) ((GameTimeController.TICKS_PER_SECOND * distance) / speed);

        // Calculate the xspeed and yspeed in unit/ticks in function of the movement speed
        _moveData._xSpeedTicks = (float) ((cos * speed) / GameTimeController.TICKS_PER_SECOND);
        _moveData._ySpeedTicks = (float) ((sin * speed) / GameTimeController.TICKS_PER_SECOND);
        _moveData._moveStartTime = GameTimeController.getGameTicks();

        /*System.out.println(_moveData._xSpeedTicks);
        System.out.println(_moveData._ySpeedTicks);
        System.out.println(_moveData._moveStartTime);
        System.out.println(_moveData._ticksToMove);*/
    }

    public boolean updatePosition(int gameTicks) {
        if (_moveData == null) {
            return true;
        }

        if (_moveData._moveTimestamp == gameTicks) {
            return false;
        }

        // Calculate the time between the beginning of the deplacement and now
        int elapsed = gameTicks - _moveData._moveStartTime;

        if (elapsed >= _moveData._ticksToMove) {
            // Set the timer of last position update to now
            _moveData._moveTimestamp = gameTicks;

            setPosition(new Point3D(_moveData._xDestination, _moveData._yDestination, _moveData._zDestination));
            System.out.println("Update position: " + new Point3D(_moveData._xDestination, _moveData._yDestination, _moveData._zDestination));

            if (_moveData.path.size() > 0) {
                followPath();
                _moveData.path.remove(0);
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
        /**
         * The _move timestamp.
         */
        public int _moveTimestamp;

        /**
         * The _x destination.
         */
        public int _xDestination;

        /**
         * The _y destination.
         */
        public int _yDestination;

        /**
         * The _z destination.
         */
        public int _zDestination;

        /**
         * The _x move from.
         */
        public int _xMoveFrom;

        /**
         * The _y move from.
         */
        public int _yMoveFrom;

        /**
         * The _z move from.
         */
        public int _zMoveFrom;

        /**
         * The _heading.
         */
        public int _heading;

        /**
         * The _move start time.
         */
        public int _moveStartTime;

        /**
         * The _ticks to move.
         */
        public int _ticksToMove;

        /**
         * The _x speed ticks.
         */
        public float _xSpeedTicks;

        /**
         * The _y speed ticks.
         */
        public float _ySpeedTicks;

        /**
         * The geo path.
         */
        public List<NodeLoc> path;
    }
}
