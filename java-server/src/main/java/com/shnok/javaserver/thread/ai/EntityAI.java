package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entity.Entity;
import com.shnok.javaserver.util.VectorUtils;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class EntityAI extends BaseAI {
    protected boolean thinking = false;

    @Override
    protected void onEvtThink() {}

    @Override
    protected void onEvtDead() {
        super.onEvtDead();

        owner.setMoving(false);
        autoAttacking = false;
        attackTarget = null;
        followTarget = null;
    }

    @Override
    protected void onEvtArrived() {}

    @Override
    protected void onEvtAttacked(Entity attacker) {
        if(getAttackTarget() == null) {
            setAttackTarget(attacker);
        }

        if(getTarget() == null) {
            setTarget(attacker);
        }

        if(getFollowTarget() == null) {
            setFollowTarget(attacker);
        }
    }

    @Override
    protected void onIntentionAttack() {
        if(attackTarget ==  null) {
            log.warn("Attack target is null");
        }

        intention = Intention.INTENTION_ATTACK;

        // If target too far follow target
        float attackRange = getOwner().getTemplate().baseAtkRange;
        if(VectorUtils.calcDistance(getOwner().getPos(), attackTarget.getPos()) > attackRange) {
            log.debug("Start moving to attacker");
            followTarget = attackTarget;
            startFollow(attackTarget, attackRange);
            return;
        }

        // Stop running if running
        if(owner.isMoving()) {
            owner.setMoving(false);
            stopFollow();
        }

        // Attack
        log.debug("Start attack");
        owner.doAttack(attackTarget);
    }

    @Override
    protected void onIntentionFollow() {

    }

    @Override
    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    @Override
    protected void onIntentionMoveTo(Point3D arg0) {
        log.debug("intention moveto!");
        intention = Intention.INTENTION_MOVE_TO;
    }

    @Override
    protected void onIntentionIdle() {
        log.debug("intention idle");
        intention = Intention.INTENTION_IDLE;
    }
}
