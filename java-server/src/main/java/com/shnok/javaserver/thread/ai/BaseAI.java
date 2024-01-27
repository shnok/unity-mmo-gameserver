package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entity.Entity;
import lombok.Data;

@Data
public abstract class BaseAI {
    protected Entity owner;
    protected boolean moving = false;
    protected EntityMovingReason movingReason;
    protected Intention intention = Intention.INTENTION_IDLE;

    public void notifyEvent(Event evt) {
        switch (evt) {
            case THINK:
                onEvtThink();
                break;
            case DEAD:
                onEvtDead();
                break;
            case ARRIVED:
                onEvtArrived();
                break;
        }
    }

    protected abstract void onEvtThink();

    protected abstract void onEvtDead();

    protected abstract void onEvtArrived();

    protected abstract void setOwner(Entity owner);

    public Intention getIntention() {
        return intention;
    }


    public void setIntention(Intention intention) {
        setIntention(intention, null);
    }

    public void setIntention(Intention intention, Object arg0) {
        switch (intention) {
            case INTENTION_IDLE:
                onIntentionIdle();
                break;
            case INTENTION_MOVE_TO:
                onIntentionMoveTo((Point3D) arg0);
                break;
        }
    }

    protected abstract void onIntentionMoveTo(Point3D arg0);

    protected abstract void onIntentionIdle();

}
