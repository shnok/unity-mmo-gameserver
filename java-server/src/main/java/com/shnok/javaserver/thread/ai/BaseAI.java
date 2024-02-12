package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.GameObject;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entity.Entity;
import com.shnok.javaserver.model.entity.PlayerInstance;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.util.VectorUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Future;

@Data
@Log4j2
public abstract class BaseAI {
    protected Entity owner;
    protected EntityMovingReason movingReason;
    protected Intention intention = Intention.INTENTION_IDLE;
    protected boolean autoAttacking;
    private GameObject target;
    private Entity castTarget;
    protected Entity attackTarget;
    protected Entity followTarget;
    protected Future<?> followTask = null;
    private static final int FOLLOW_INTERVAL = 1000;
    private static final int ATTACK_FOLLOW_INTERVAL = 500;

    public void notifyEvent(Event evt) {
        notifyEvent(evt, null);
    }

    public void notifyEvent(Event evt, GameObject go) {
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
            case ATTACKED:
                onEvtAttacked((Entity) go);
                break;
        }
    }

    protected abstract void onEvtThink();

    protected void onEvtDead() {
        stopFollow();
    }

    protected abstract void onEvtArrived();

    protected abstract void setOwner(Entity owner);

    protected abstract void onEvtAttacked(Entity attacker);

    public void setIntention(Intention intention) {
        setIntention(intention, null);
    }

    public void setIntention(Intention intention, Object arg0) {
        if ((intention != Intention.INTENTION_FOLLOW) && (intention != Intention.INTENTION_ATTACK)) {
            stopFollow();
        }

        switch (intention) {
            case INTENTION_IDLE:
                onIntentionIdle();
                break;
            case INTENTION_MOVE_TO:
                onIntentionMoveTo((Point3D) arg0);
                break;
            case INTENTION_ATTACK:
                onIntentionAttack();
                break;
            case INTENTION_FOLLOW:
                onIntentionFollow();
                break;
        }
    }

    protected abstract void onIntentionAttack();

    protected abstract void onIntentionFollow();

    protected abstract void onIntentionMoveTo(Point3D arg0);

    protected abstract void onIntentionIdle();

    public void setTarget(Entity target) {
        log.debug("[{}] New target [{}]", owner.getId(), target != null ? target.getId() : "null");
        this.target = target;
    }

    // Create and Launch an AI Follow Task to execute every 1s
    public synchronized void startFollow(Entity target) {
        if (followTask != null) {
            followTask.cancel(false);
            followTask = null;
        }

        followTarget = target;
        followTask = ThreadPoolManagerService.getInstance().scheduleAiAtFixedRate(new FollowTask(), 5, FOLLOW_INTERVAL);
    }

    // Create and Launch an AI Follow Task to execute every 0.5s, following at specified range.
    public synchronized void startFollow(Entity target, float range) {
        if (followTask != null) {
            followTask.cancel(false);
            followTask = null;
        }

        movingReason = EntityMovingReason.Running;
        followTarget = target;
        followTask = ThreadPoolManagerService.getInstance().scheduleAiAtFixedRate(new FollowTask(range), 5, ATTACK_FOLLOW_INTERVAL);
    }

    // Stop the Follow Task
    public synchronized void stopFollow() {
        if (followTask != null) {

            followTask.cancel(false);
            followTask = null;
        }
        followTarget = null;
    }

    // Move to follow target
    protected void moveToTarget(Entity entity, float distance) {
        // Chek if entity can move
        if (!owner.canMove()) {
            return;
        }

        // Set AI movement data
        target = entity;

        if ((target == null) || (owner == null)) {
            return;
        }

        // Calculate movement data for a move to location action and add the actor to movingObjects of GameTimeController
        // TODO: add range
        log.warn("Follow! Current distance: {} Attack distance: {} Speed: {}", VectorUtils.calcDistance2D(owner.getPos(), entity.getPos()), distance, entity.getStatus().getMoveSpeed());
        owner.moveTo(new Point3D(entity.getPos()), distance);

        if (!owner.isMoving()) {
            log.warn("Follow failed");
            return;
        }

        // TODO: share state with client

    }

    @AllArgsConstructor
    @NoArgsConstructor
    class FollowTask implements Runnable {
        protected float range = 60;

        @Override
        public void run() {
            try {
                if (followTask == null) {
                    return;
                }

                if (followTarget == null) {
                    stopFollow();
                    return;
                }

                if (VectorUtils.calcDistance2D(target.getPos(), getOwner().getPos()) > range) {
                    moveToTarget(followTarget, range);
                }
            } catch (Throwable t) {
                log.warn(t);
            }
        }
    }
}
