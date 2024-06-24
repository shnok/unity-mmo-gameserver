package com.shnok.javaserver.model.object.entity;

import com.shnok.javaserver.config.ServerConfig;
import com.shnok.javaserver.db.entity.DBSpawnList;
import com.shnok.javaserver.dto.serverpackets.ApplyDamagePacket;
import com.shnok.javaserver.dto.serverpackets.ObjectMoveToPacket;
import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.model.knownlist.NpcKnownList;
import com.shnok.javaserver.model.status.NpcStatus;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.model.template.NpcTemplate;
import com.shnok.javaserver.service.SpawnManagerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.thread.ai.BaseAI;
import com.shnok.javaserver.thread.ai.NpcAI;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import static com.shnok.javaserver.config.Configuration.serverConfig;

@Data
@EqualsAndHashCode(callSuper=false)
@Log4j2
public class NpcInstance extends Entity {
    private boolean isStatic;
    private boolean randomWalk;
    private DBSpawnList spawnInfo;
    private int leftHandId;
    private int rightHandId;

    public NpcInstance(int id, NpcTemplate npcTemplate) {
        super(id);
        this.template = npcTemplate;

        this.leftHandId = npcTemplate.lhand;
        this.rightHandId = npcTemplate.rhand;

        this.status = new NpcStatus(npcTemplate.getLevel(), npcTemplate.baseHpMax);
        this.isStatic = npcTemplate.getNpcClass().contains("NPC");
        this.randomWalk = false;
    }

    @Override
    public void inflictDamage(Entity attacker, int value) {
        super.inflictDamage(attacker, value);

        if(isStatic()) {
            status.setHp(Math.max(status.getHp() - value, 1));
        } else {
            status.setHp(Math.max(status.getHp() - value, 0));
        }

        if (status.getHp() == 0) {
            onDeath();
        }
    }

    @Override
    public boolean onHitTimer(Entity target, int damage, boolean criticalHit) {
        if(super.onHitTimer(target, damage, criticalHit)) {
            ApplyDamagePacket applyDamagePacket = new ApplyDamagePacket(
                    getId(), target.getId(), damage, target.getStatus().getHp(), criticalHit);
            broadcastPacket(applyDamagePacket);
            return true;
        }

        return false;
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
                sendPacketToPlayer(player, new ObjectMoveToPacket(
                        getId(), moveData.destination,
                        getStatus().getMoveSpeed(),
                        getAi().getMovingReason() == EntityMovingReason.Walking));
                break;
            case INTENTION_IDLE:
            case INTENTION_WAITING:
        }

        return true;
    }

    /* remove and stop AI */
    public void stopAndRemoveAI() {
        BaseAI ai = getAi();
        if(serverConfig.printAi()) {
            log.debug("[{}] Stop and remove AI", getId());
        }
        if(ai instanceof NpcAI) {
            ((NpcAI) ai).stopAITask();
            setAi(null);
        }
    }

    /* add AI to NPC */
    public void refreshAI() {
        if (!isStatic()) {
            if(serverConfig.printAi()) {
                log.debug("[{}] Add AI", getId());
            }
            if(getAi() != null) {
                stopAndRemoveAI();
            }

            NpcAI ai = new NpcAI(this);
//            TestAI ai = new TestAI(this, Arrays.asList(
//                    new Point3D(4726.871f, -68.61623f, -1732.926f),
//                    new Point3D(4733.977f, -68.61623f, -1722.304f)
//                     ));
            setAi(ai);
        }
    }
}
