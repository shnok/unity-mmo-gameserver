package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.dto.serverpackets.ObjectPositionPacket;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entity.Entity;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class EntityAI extends BaseAI {
    protected boolean thinking = false;

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
            log.debug("Entity is attacking a new target");
            notifyEvent(Event.CANCEL);
            setAttackTarget(entity);
            intention = Intention.INTENTION_ATTACK;
        } else if(attackTarget == null || attackTarget.isDead()) {
            log.warn("Attack target is null or dead");
            // TODO return to spawn...
            setIntention(Intention.INTENTION_IDLE);
        }
    }

    @Override
    protected void onIntentionFollow() {}

    @Override
    protected void onIntentionMoveTo(Point3D arg0) {
        log.debug("intention moveto!");
        intention = Intention.INTENTION_MOVE_TO;
    }

    @Override
    protected void onIntentionIdle() {
        log.debug("intention idle");
    }
}
