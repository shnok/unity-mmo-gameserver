package com.shnok.javaserver.model.entities;

import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.service.SpawnManagerService;
import com.shnok.javaserver.model.SpawnInfo;
import com.shnok.javaserver.model.status.NpcStatus;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.dto.serverpackets.RemoveObject;
import org.springframework.beans.factory.annotation.Autowired;

public class NpcInstance extends Entity {
    @Autowired
    private WorldManagerService worldManagerService;
    @Autowired
    private SpawnManagerService spawnManagerService;

    private boolean isStatic = false;
    private boolean patrol = false;
    private boolean randomWalk = false;
    private Point3D[] patrolWaypoints;
    private int npcId;
    private NpcStatus status;
    private SpawnInfo spawnInfo;

    public NpcInstance(int npcId, NpcStatus status, boolean isStatic, boolean randomWalk, boolean patrol, Point3D[] patrolWaypoints) {
        this.npcId = npcId;
        this.status = status;
        this.isStatic = isStatic;
        this.randomWalk = randomWalk;
        this.patrol = patrol;
        this.patrolWaypoints = patrolWaypoints;
    }

    public NpcInstance(int id, int npcId) {
        super(id);
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    @Override
    public void inflictDamage(int value) {
        status.setCurrentHp(status.getCurrentHp() - value);

        if (status.getCurrentHp() <= 0) {
            onDeath();
        }
    }

    @Override
    public NpcStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = (NpcStatus) status;
    }

    @Override
    public boolean canMove() {
        return canMove && !isStatic;
    }

    @Override
    public void onDeath() {
        if (ai != null)
            ai.notifyEvent(Event.DEAD);

        worldManagerService.removeNPC(this);
        serverService.broadcastAll(new RemoveObject(getId()));
        spawnManagerService.respawn(getSpawn().getId());
    }

    public SpawnInfo getSpawn() {
        return spawnInfo;
    }

    public void setSpawn(SpawnInfo spawnInfo) {
        this.spawnInfo = spawnInfo;
    }

    public boolean doPatrol() {
        return patrol;
    }

    public void setPatrol(boolean patrol) {
        this.patrol = patrol;
    }

    public Point3D[] getPatrolWaypoints() {
        return patrolWaypoints;
    }

    public void setPatrolWaypoints(Point3D[] patrolWaypoints) {
        this.patrolWaypoints = patrolWaypoints;
    }

    public boolean doRandomWalk() {
        return randomWalk;
    }

    public void setRandomWalk(boolean randomWalk) {
        this.randomWalk = randomWalk;
    }
}
