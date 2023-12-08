package com.shnok.javaserver.model.entity;

import com.shnok.javaserver.db.entity.Npc;
import com.shnok.javaserver.db.entity.SpawnList;
import com.shnok.javaserver.dto.serverpackets.RemoveObjectPacket;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.status.NpcStatus;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.model.template.NpcTemplate;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.service.SpawnManagerService;
import com.shnok.javaserver.service.WorldManagerService;

public class NpcInstance extends Entity {
    private boolean isStatic = false;
    private boolean patrol = false;
    private boolean randomWalk = false;
    private Point3D[] patrolWaypoints;
    private SpawnList spawnInfo;

    public NpcInstance(int id, NpcTemplate npcTemplate, boolean patrol, Point3D[] patrolWaypoints) {
        super(id);
        this.template = npcTemplate;
        this.status = new NpcStatus(npcTemplate.getLevel(), npcTemplate.baseHpMax);
        this.isStatic = false;
        this.randomWalk = false;
        this.patrol = patrol;
        this.patrolWaypoints = patrolWaypoints;
    }

    public NpcInstance(int id, NpcTemplate npcTemplate, boolean randomWalk, boolean patrol, Point3D[] patrolWaypoints) {
        super(id);
        this.template = npcTemplate;
        this.status = new NpcStatus(npcTemplate.getLevel(), npcTemplate.baseHpMax);
        this.isStatic = false;
        this.randomWalk = randomWalk;
        this.patrol = false;
        this.patrolWaypoints = patrolWaypoints;
    }

    public NpcInstance(int id, NpcTemplate npcTemplate) {
        super(id);
        this.template = npcTemplate;
        this.status = new NpcStatus(npcTemplate.getLevel(), npcTemplate.baseHpMax);
        this.isStatic = true;
        this.randomWalk = false;
        this.patrol = false;
    }

    public NpcInstance(int id, int npcId) {
        super(id);
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }

    @Override
    public void inflictDamage(int value) {
        status.setHp(status.getHp() - value);

        if (status.getHp() <= 0) {
            onDeath();
        }
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

        WorldManagerService.getInstance().removeNPC(this);
        ServerService.getInstance().broadcast(new RemoveObjectPacket(getId()));
        SpawnManagerService.getInstance().respawn(spawnInfo, (NpcTemplate) template);
    }

    public SpawnList getSpawn() {
        return spawnInfo;
    }

    public void setSpawn(SpawnList spawnInfo) {
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

    @Override
    public final NpcTemplate getTemplate() {
        return (NpcTemplate) super.getTemplate();
    }

    @Override
    public final NpcStatus getStatus() {
        return (NpcStatus) super.getStatus();
    }
}
