package com.shnok.javaserver.model.entity;

import com.shnok.javaserver.db.entity.SpawnList;
import com.shnok.javaserver.dto.serverpackets.ObjectAnimationPacket;
import com.shnok.javaserver.dto.serverpackets.ObjectMoveToPacket;
import com.shnok.javaserver.dto.serverpackets.ObjectPositionPacket;
import com.shnok.javaserver.dto.serverpackets.RemoveObjectPacket;
import com.shnok.javaserver.enums.EntityAnimation;
import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.knownlist.NpcKnownList;
import com.shnok.javaserver.model.status.NpcStatus;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.model.template.NpcTemplate;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.service.SpawnManagerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
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
    private boolean isStatic;
    private boolean randomWalk;
    private SpawnList spawnInfo;

    public NpcInstance(int id, NpcTemplate npcTemplate) {
        super(id);
        this.template = npcTemplate;
        this.status = new NpcStatus(npcTemplate.getLevel(), npcTemplate.baseHpMax);
        this.isStatic = true;
        this.randomWalk = false;
    }

    @Override
    public void inflictDamage(int value) {
        status.setHp(Math.max(status.getHp() - value, 0));

        if (status.getHp() == 0) {
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
        super.onDeath();

        // Tell client that entity died
        //TODO Tell client that entity died

        // Destroy the gameobject after 5 seconds
        ThreadPoolManagerService.getInstance().scheduleDestroyObject(new ScheduleDestroyTask(this), 5000);
    }

    @Override
    public void destroy() {
        super.destroy();

        // Remove npc from npc list and schedule respawn
        WorldManagerService.getInstance().removeNPC(this);
        SpawnManagerService.getInstance().respawn(spawnInfo, (NpcTemplate) template);
    }

    @Override
    public final NpcTemplate getTemplate() {
        return (NpcTemplate) super.getTemplate();
    }

    @Override
    public final NpcStatus getStatus() {
        return (NpcStatus) super.getStatus();
    }

    @Override
    public boolean shareCurrentAction(PlayerInstance player) {
        if(!super.shareCurrentAction(player)) {
            return false;
        }

        switch (getAi().getIntention()) {
            case INTENTION_MOVE_TO:
                sendPacketToPlayer(player, new ObjectMoveToPacket(getId(), moveData.destination, getStatus().getMoveSpeed()));

                if(getAi().getMovingReason() == EntityMovingReason.Walking) {
                    sendPacketToPlayer(player, new ObjectAnimationPacket(
                            getId(), EntityAnimation.Walk.getValue(), 1f));
                } else if(getAi().getMovingReason() == EntityMovingReason.Running) {
                    sendPacketToPlayer(player, new ObjectAnimationPacket(
                            getId(), EntityAnimation.Walk.getValue(), 1f));
                }
                break;
            case INTENTION_IDLE:
            case INTENTION_WAITING:
        }

        return true;
    }

    /* remove and stop AI */
    public void stopAndRemoveAI() {
        BaseAI ai = getAi();
//        log.debug("[{}] Stop and remove AI", getId());
        if(ai instanceof NpcAI) {
            ((NpcAI) ai).stopAITask();
            setAi(null);
        }
    }

    /* add AI to NPC */
    public void refreshAI() {
        if (!isStatic()) {
//            log.debug("[{}] Add AI", getId());
            if(getAi() != null) {
                stopAndRemoveAI();
            }

            NpcAI ai = new NpcAI();
            ai.setOwner(this);
            setAi(ai);
        }
    }
}
