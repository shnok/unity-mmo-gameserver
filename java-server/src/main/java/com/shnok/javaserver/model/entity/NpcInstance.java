package com.shnok.javaserver.model.entity;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.db.entity.SpawnList;
import com.shnok.javaserver.dto.serverpackets.RemoveObjectPacket;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.knownlist.NpcKnownList;
import com.shnok.javaserver.model.status.NpcStatus;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.model.template.NpcTemplate;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.service.SpawnManagerService;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.thread.ai.BaseAI;
import com.shnok.javaserver.thread.ai.NpcAI;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

@Data
@EqualsAndHashCode(callSuper=false)
@Log4j2
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

    @Override
    public void inflictDamage(int value) {
        status.setHp(status.getHp() - value);

        if (status.getHp() <= 0) {
            onDeath();
        }
    }

    @Override
    public NpcKnownList getKnownList() {
        if ((super.getKnownList() == null) || !(super.getKnownList() instanceof NpcKnownList)) {
            setKnownList(new NpcKnownList(this));
        }
        return (NpcKnownList) super.getKnownList();
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

    public boolean doPatrol() {
        return patrol;
    }

    public void setPatrol(boolean patrol) {
        this.patrol = patrol;
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

    /* remove and stop AI */
    public void stopAndRemoveAI() {
        BaseAI ai = getAi();
        log.debug("[{}] Stop and remove AI", getId());
        if(ai instanceof NpcAI) {
            ((NpcAI) ai).stopAITask();
            setAi(null);
        }
    }

    /* add AI to NPC */
    public void refreshAI() {
        if (!isStatic()) {
            log.debug("[{}] Add AI", getId());
            if(getAi() != null) {
                stopAndRemoveAI();
            }

            NpcAI ai = new NpcAI();
            ai.setOwner(this);
            setAi(ai);
        }
    }
}
