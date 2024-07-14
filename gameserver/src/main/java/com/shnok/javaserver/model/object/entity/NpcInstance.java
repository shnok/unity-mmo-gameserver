package com.shnok.javaserver.model.object.entity;

import com.shnok.javaserver.db.entity.DBArmor;
import com.shnok.javaserver.db.entity.DBSpawnList;
import com.shnok.javaserver.db.entity.DBWeapon;
import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.dto.external.serverpackets.ApplyDamagePacket;
import com.shnok.javaserver.dto.external.serverpackets.ObjectMoveToPacket;
import com.shnok.javaserver.dto.external.serverpackets.SystemMessagePacket;
import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.enums.network.SystemMessageId;
import com.shnok.javaserver.model.knownlist.NpcKnownList;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.status.NpcStatus;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.model.template.NpcTemplate;
import com.shnok.javaserver.service.SpawnManagerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.service.db.ItemTable;
import com.shnok.javaserver.thread.ai.BaseAI;
import com.shnok.javaserver.thread.ai.NpcAI;
import com.shnok.javaserver.thread.entity.ScheduleDestroyTask;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import static com.shnok.javaserver.config.Configuration.server;

@Data
@EqualsAndHashCode(callSuper=false)
@Log4j2
public class NpcInstance extends Entity {
    private boolean isStatic;
    private boolean randomWalk;
    private DBSpawnList spawnInfo;
    private int leftHandId;
    private int rightHandId;
    private ItemInstance leftHandItem;
    private ItemInstance rightHandItem;

    public NpcInstance(int id, NpcTemplate npcTemplate) {
        super(id, npcTemplate);

        this.leftHandId = npcTemplate.lhand;
        this.rightHandId = npcTemplate.rhand;

        if(leftHandId != 0) {
            leftHandItem = new ItemInstance(id, ItemTable.getInstance().getItemById(leftHandId));
        }

        if(rightHandId != 0) {
            rightHandItem = new ItemInstance(id, ItemTable.getInstance().getItemById(rightHandId));
        }

        this.status = new NpcStatus(this);
        this.isStatic = npcTemplate.getNpcClass().contains("NPC");
        this.randomWalk = false;
    }

    @Override
    public boolean onHitTimer(ApplyDamagePacket attack, Entity target, int damage, boolean crit, boolean miss, boolean soulshot, byte shld) {
        if(super.onHitTimer(attack, target, damage, crit, miss, soulshot, shld)) {
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
    public boolean sendPacket(SendablePacket packet) {
        return true;
    }

    @Override
    public void doDie(Entity attacker) {
        super.doDie(attacker);

        // Tell client that entity died
        //TODO Tell client that entity died

        SystemMessagePacket sm = new SystemMessagePacket(SystemMessageId.YOU_EARNED_S1_EXP_AND_S2_SP);
        sm.addInt(0);
        sm.addInt(0);
        sm.writeMe();

        attacker.sendPacket(sm);
        //TODO: give exp?
        //TODO: Share HP

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
                        getStat().getMoveSpeed(),
                        getAi().getMovingReason() == EntityMovingReason.Walking));
                break;
            case INTENTION_IDLE:
            case INTENTION_WAITING:
        }

        return true;
    }

    @Override
    public ItemInstance getActiveWeaponInstance() {
        return getRightHandItem();
    }

    @Override
    public ItemInstance getSecondaryWeaponInstance() {
        return getLeftHandItem();
    }

    @Override
    public DBWeapon getActiveWeaponItem() {
        if(rightHandItem != null && rightHandItem.getItem() instanceof DBWeapon) {
            return (DBWeapon) rightHandItem.getItem();
        }
        if(leftHandItem != null && leftHandItem.getItem() instanceof DBWeapon) {
            return (DBWeapon) leftHandItem.getItem();
        }

        return null;
    }

    @Override
    public DBArmor getSecondaryWeaponItem() {
        if(leftHandItem != null && leftHandItem.getItem() instanceof DBArmor) {
            return (DBArmor) leftHandItem.getItem();
        }

        return null;
    }

    /* remove and stop AI */
    public void stopAndRemoveAI() {
        BaseAI ai = getAi();
        if(server.printAi()) {
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
            if(server.printAi()) {
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

    public void addAttackerToAttackByList(Entity player) {
//        if ((player == null) || (player == this) || getAttackByList().contains(player)) {
//            return;
//        }
//
//        getAttackByList().add(player);
        //TODO: Hanlde aggro and rewards based on attacklist
    }
}
