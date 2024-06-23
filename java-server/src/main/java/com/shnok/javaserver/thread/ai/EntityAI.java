package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.config.ServerConfig;
import com.shnok.javaserver.dto.serverpackets.ObjectPositionPacket;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.object.entity.Entity;
import lombok.extern.log4j.Log4j2;

import static com.shnok.javaserver.config.Configuration.serverConfig;

@Log4j2
public class EntityAI extends BaseAI {
    protected boolean thinking = false;

    public EntityAI(Entity owner) {
        super(owner);
    }

    /*
    =========================
    ========= EVENT =========
    =========================
     */
    @Override
    protected void onEvtThink() {}

    @Override
    protected void onEvtDead() {
        stopFollow();
        owner.setMoving(false);
        autoAttacking = false;
        clearTarget();
    }

    @Override
    protected void onEvtArrived() {
        /* share new position with known players */
        ObjectPositionPacket packet = new ObjectPositionPacket(owner.getId(), owner.getPos());
        getOwner().broadcastPacket(packet);
    }

    @Override
    protected void onEvtAttacked(Entity attacker) {
        // Set target if target was null
        if(getTarget() == null) {
            setTarget(attacker);
        }
    }

    @Override
    protected void onEvtForgetObject(Entity object) {
        clearTarget();

        if(getIntention() == Intention.INTENTION_ATTACK) {
            setIntention(Intention.INTENTION_IDLE);
        }

        if(getIntention() == Intention.INTENTION_FOLLOW) {
            setIntention(Intention.INTENTION_IDLE);
        }
    }

    @Override
    protected void onEvtReadyToAct() {
        onEvtThink();
    }

    @Override
    protected void onEvtCancel() {
        stopFollow();
        clientStopAutoAttack();
    }

    /*
    =========================
    ======= INTENTION =======
    =========================
     */
    @Override
    protected void onIntentionAttack(Entity entity) {
        if(entity != null && (attackTarget != entity || !isAutoAttacking())) {
            if(serverConfig.printAi()) {
                log.debug("[AI][{}] Entity is attacking a new target", owner.getId());
            }
            notifyEvent(Event.CANCEL);
            setAttackTarget(entity);
            System.out.println(intention);
        } else if(attackTarget == null || attackTarget.isDead()) {
            if(serverConfig.printAi()) {
                log.warn("[AI][{}] Attack target is null or dead", owner.getId());
            }
            // TODO return to spawn...
            setIntention(Intention.INTENTION_IDLE);
        }
    }

    @Override
    protected void onIntentionFollow() {}

    @Override
    protected void onIntentionMoveTo(Point3D arg0) {
        if(serverConfig.printAi()) {
            log.debug("[AI][{}] Intention MoveTo", owner.getId());
        }
    }

    @Override
    protected void onIntentionIdle() {
        if(serverConfig.printAi()) {
            log.debug("[AI][{}] Intention Idle", owner.getId());
        }
    }
}
