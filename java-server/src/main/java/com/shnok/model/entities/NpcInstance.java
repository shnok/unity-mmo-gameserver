package com.shnok.model.entities;

import com.shnok.Server;
import com.shnok.World;
import com.shnok.ai.enums.Event;
import com.shnok.model.Point3D;
import com.shnok.model.spawner.SpawnHandler;
import com.shnok.model.spawner.SpawnInfo;
import com.shnok.model.status.NpcStatus;
import com.shnok.model.status.Status;
import com.shnok.serverpackets.RemoveObject;

public class NpcInstance extends Entity {
    private boolean _isStatic = false;
    private boolean _patrol = false;
    private boolean _randomWalk = false;
    private Point3D[] _patrolWaypoints;
    private int _npcId;
    private NpcStatus _status;
    private SpawnInfo _spawnInfo;

    public NpcInstance(int npcId, NpcStatus status, boolean isStatic, boolean randomWalk, boolean patrol, Point3D[] patrolWaypoints) {
        _npcId = npcId;
        _status = status;
        _isStatic = isStatic;
        _randomWalk = randomWalk;
        _patrol = patrol;
        _patrolWaypoints = patrolWaypoints;
    }

    public NpcInstance(int id, int npcId) {
        super(id);
    }

    public int getNpcId() {
        return _npcId;
    }

    public void setNpcId(int npcId) {
        _npcId = npcId;
    }

    public boolean isStatic() {
        return _isStatic;
    }

    public void setStatic(boolean isStatic) {
        _isStatic = isStatic;
    }

    @Override
    public void inflictDamage(int value) {
        _status.setCurrentHp(_status.getCurrentHp() - value);

        if (_status.getCurrentHp() <= 0) {
            onDeath();
        }
    }

    @Override
    public NpcStatus getStatus() {
        return _status;
    }

    @Override
    public void setStatus(Status status) {
        _status = (NpcStatus) status;
    }

    @Override
    public boolean canMove() {
        return _canMove && !_isStatic;
    }

    @Override
    public void onDeath() {
        if (_ai != null)
            _ai.notifyEvent(Event.DEAD);

        World.getInstance().removeNPC(this);
        Server.getInstance().broadcastAll(new RemoveObject(getId()));
        SpawnHandler.getInstance().respawn(getSpawn().getId());
    }

    public SpawnInfo getSpawn() {
        return _spawnInfo;
    }

    public void setSpawn(SpawnInfo spawnInfo) {
        _spawnInfo = spawnInfo;
    }

    public boolean doPatrol() {
        return _patrol;
    }

    public void setPatrol(boolean patrol) {
        this._patrol = patrol;
    }

    public Point3D[] getPatrolWaypoints() {
        return _patrolWaypoints;
    }

    public void setPatrolWaypoints(Point3D[] patrolWaypoints) {
        this._patrolWaypoints = patrolWaypoints;
    }

    public boolean doRandomWalk() {
        return _randomWalk;
    }

    public void setRandomWalk(boolean randomWalk) {
        this._randomWalk = randomWalk;
    }
}
