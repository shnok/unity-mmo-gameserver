package com.shnok.ai;

import com.shnok.ai.enums.Event;
import com.shnok.ai.enums.Intention;
import com.shnok.model.Point3D;
import com.shnok.model.entities.Entity;

public abstract class BaseAI {
    protected Entity _owner;
    protected boolean _moving = false;
    protected Intention _intention = Intention.INTENTION_IDLE;

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

    public void setOwner(Entity owner) {
        _owner = owner;
    }

    public Intention getIntention() {
        return _intention;
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
