package com.shnok.ai;

import com.shnok.ai.enums.Event;
import com.shnok.model.GameObject;
import com.shnok.model.entities.Entity;

import java.util.concurrent.Future;

public abstract class BaseAI {
    protected Entity _owner;
    protected boolean _moving = false;

    protected void moveTo(int x, int y, int z) {
        if (_owner.canMove()) {

            if(_owner.moveTo(x, y, z)) {
                _moving = true;
                // Send a packet to notify npc moving
            }


        }
    }

    protected void stopMove() {
        _moving = false;

        // Send a packet to notify npc stop moving
    }

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

}
