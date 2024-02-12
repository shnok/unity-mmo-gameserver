package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entity.Entity;

public class EntityAI extends BaseAI {
    protected boolean thinking = false;

    @Override
    protected void onEvtThink() {

    }

    @Override
    protected void onEvtDead() {
        moving = false;
        autoAttacking = false;
        attackTarget = null;
        followTarget = null;
    }

    @Override
    protected void onEvtArrived() {

    }

    @Override
    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    @Override
    protected void onIntentionMoveTo(Point3D arg0) {
        intention = Intention.INTENTION_MOVE_TO;
    }

    @Override
    protected void onIntentionIdle() {

    }
}
