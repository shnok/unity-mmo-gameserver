package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.util.VectorUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PlayerAI extends EntityAI {
    public PlayerAI(Entity owner) {
        super(owner);
    }

    @Override
    protected void onEvtThink() {
        if (thinking || owner == null) {
            return;
        }

        thinking = true;

        try {
            if(getIntention() == Intention.INTENTION_ATTACK) {
                thinkAttack();
            }
        } catch (NullPointerException e) {
            log.warn("Lost target during attack loop");
            //TODO: teleport to spawn if too far on next patrol
            return;
        }

        thinking = false;
    }

    void thinkAttack() {
        if(attackTarget == null || !owner.getKnownList().knowsObject(attackTarget) || attackTarget.isDead()) {
            log.warn("Attack target is null or dead");
            attackTarget = null;
            notifyEvent(Event.CANCEL);
            setIntention(Intention.INTENTION_IDLE);
            return;
        }

        // If target too far follow target
        float attackRange = getOwner().getTemplate().baseAtkRange;
        if(VectorUtils.calcDistance2D(getOwner().getPos(), attackTarget.getPos()) > attackRange) {
            //TODO: Wait for attack to finish

            // Stop auto attacking
            notifyEvent(Event.CANCEL);

            log.debug("Start moving to attacker");
            return;
        }

        // Attack
        log.debug("Start attack");
        owner.doAttack(attackTarget);
    }

    @Override
    protected void onEvtDead() {}

    @Override
    protected void onEvtArrived() {}

    @Override
    protected void onIntentionMoveTo(Point3D arg0) {}

    @Override
    protected void onIntentionIdle() {
        intention = Intention.INTENTION_IDLE;
    }

    @Override
    protected void onEvtReadyToAct() {
        onEvtThink();
    }

    @Override
    protected void onEvtCancel() {
        setIntention(Intention.INTENTION_IDLE);
        clientStopAutoAttack();
    }
}
