package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.config.ServerConfig;
import com.shnok.javaserver.dto.serverpackets.AutoAttackStartPacket;
import com.shnok.javaserver.dto.serverpackets.AutoAttackStopPacket;
import com.shnok.javaserver.dto.serverpackets.EntitySetTargetPacket;
import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.object.GameObject;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.util.VectorUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.Future;

import static com.shnok.javaserver.config.Configuration.serverConfig;

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

    public BaseAI(Entity owner) {
        this.owner = owner;
    }

    /*
    =========================
    ========= EVENT =========
    =========================
     */
    public void notifyEvent(Event evt) {
        notifyEvent(evt, null);
    }

    public void notifyEvent(Event evt, GameObject go) {
//        log.debug("[AI] New event: {}", evt);

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
            case FORGET_OBJECT:
                onEvtForgetObject((Entity) go);
                break;
            case READY_TO_ACT:
                onEvtReadyToAct();
                break;
            case CANCEL:
                onEvtCancel();
                break;
        }
    }

    protected abstract void onEvtThink();

    protected abstract void onEvtDead();

    protected abstract void onEvtArrived();

    protected abstract void onEvtAttacked(Entity attacker);

    protected abstract void onEvtForgetObject(Entity object);

    protected abstract void onEvtReadyToAct();

    protected abstract void onEvtCancel();

    /*
    =========================
    ======= INTENTION =======
    =========================
    */
    public void setIntention(Intention intention) {
        setIntention(intention, null);
    }

    public void setIntention(Intention intention, Object arg0) {
        if(serverConfig.printAi()) {
            log.debug("[AI][{}] New intention: {}", getOwner().getId(), intention);
        }
        this.intention = intention;

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
                onIntentionAttack((Entity) arg0);
                break;
            case INTENTION_FOLLOW:
                onIntentionFollow();
                break;
        }
    }

    protected abstract void onIntentionAttack(Entity entity);

    protected abstract void onIntentionFollow();

    protected abstract void onIntentionMoveTo(Point3D arg0);

    protected abstract void onIntentionIdle();

    /*
    =========================
    ========= OTHER =========
    =========================
    */
    public void setTarget(Entity target) {
        if(getTarget() != target) {
            if(serverConfig.printAi()) {
                log.debug("[AI][{}] New target [{}]", owner.getId(), target != null ? target.getId() : "null");
            }
            this.target = target;

            // Sharing target with known list
            if(target != null) {
                getOwner().broadcastPacket(new EntitySetTargetPacket(getOwner().getId(), target.getId()));
            } else {
                getOwner().broadcastPacket(new EntitySetTargetPacket(getOwner().getId(), -1));
            }
        }
    }

    public synchronized void clearTarget() {
        attackTarget = null;
        followTarget = null;
        target = null;
    }

    public synchronized void setAttackTarget(Entity target) {
        setTarget(target);
        attackTarget = target;
    }

    public synchronized void setFollowTarget(Entity target) {
        setTarget(target);
        followTarget = target;
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
        System.out.println("Start follow");
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
        log.warn("Follow! Current distance: {} Attack distance: {} Speed: {}",
                VectorUtils.calcDistance2D(owner.getPos(), entity.getPos()), distance, entity.getStatus().getMoveSpeed());
        owner.moveTo(new Point3D(entity.getPos()), distance);

        if (!owner.isMoving()) {
            log.warn("Follow failed");
            return;
        }

        // TODO: share state with client

    }

    // Start the auto attack client side
    public void clientStartAutoAttack(Entity target) {
        if (!isAutoAttacking()) {
            log.debug("[AI] Client start auto attack");

            // Send a Server->Client packet AutoAttackStart to the actor and all PlayerInstances in its knownPlayers
            AutoAttackStartPacket packet = new AutoAttackStartPacket(owner.getId());
            owner.broadcastPacket(packet);

            if(owner instanceof PlayerInstance) {
                ((PlayerInstance) owner).sendPacket(packet);
            }
            setAutoAttacking(true);
        }
    }

    // Stop the auto attack client side
    public void clientStopAutoAttack() {
        if (isAutoAttacking()) {
            if(serverConfig.printAi()) {
                log.debug("[AI][{}] Client stop auto attack", owner.getId());
            }
            // Send a Server->Client packet AutoAttackStop to the actor and all PlayerInstances in its knownPlayers
            AutoAttackStopPacket packet = new AutoAttackStopPacket(owner.getId());
            owner.broadcastPacket(packet);
            if(owner instanceof PlayerInstance) {
                ((PlayerInstance) owner).sendPacket(packet);
            }
            setAutoAttacking(false);
        }
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

                System.out.println("Followtask distance: " + VectorUtils.calcDistance2D(target.getPos(), getOwner().getPos()) + " range: " + range);

                if (VectorUtils.calcDistance2D(target.getPos(), getOwner().getPos()) > range) {
                    moveToTarget(followTarget, range);
                }
            } catch (Throwable t) {
                log.warn(t);
            }
        }
    }
}
