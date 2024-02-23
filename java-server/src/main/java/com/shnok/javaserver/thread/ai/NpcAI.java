package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.pathfinding.node.Node;
import com.shnok.javaserver.service.GameTimeControllerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.object.entity.NpcInstance;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.util.VectorUtils;
import lombok.extern.log4j.Log4j2;

import java.util.Random;
import java.util.concurrent.Future;

@Log4j2
public class NpcAI extends EntityAI implements Runnable {
    private NpcInstance npc;
    private Future<?> aiTask;

    public NpcAI() {
        startAITask();
    }

    @Override
    public void run() {
        onEvtThink();
    }

    private void startAITask() {
        if (aiTask == null) {
            aiTask = ThreadPoolManagerService.getInstance().scheduleAiAtFixedRate(this, 1000,
                    Config.AI_LOOP_RATE_MS);
        }
    }

    public void stopAITask() {
        if (aiTask != null) {
            if (getIntention() == Intention.INTENTION_MOVE_TO) {
                GameTimeControllerService.getInstance().removeMovingObject(owner);
            }

            aiTask.cancel(true);
            aiTask = null;
        }
    }

    /*
    =========================
    ========= EVENT =========
    =========================
     */
    @Override
    protected void onEvtThink() {
        if (thinking || owner == null) {
            return;
        }

        if(npc == null) {
            npc = (NpcInstance) owner;
        }

        thinking = true;

        /* Is NPC waiting ? */
        if (getIntention() == Intention.INTENTION_IDLE) {
            thinkIdle();
        }

        try {
            if(getIntention() == Intention.INTENTION_ATTACK) {
                thinkAttack();
            }
        } catch (NullPointerException e) {
            if(Config.PRINT_AI_LOGS) {
                log.warn("[AI][{}] Lost target during attack loop", owner.getId());
            }
            //TODO: teleport to spawn if too far on next patrol
            return;
        }

        thinking = false;
        startAITask();
    }

    @Override
    protected void onEvtDead() {
        super.onEvtDead();

        stopAITask();
    }

    @Override
    protected void onEvtArrived() {
        super.onEvtArrived();

        if (owner.moveToNextRoutePoint()) {
            return;
        }

        if (getIntention() == Intention.INTENTION_MOVE_TO) {
            setIntention(Intention.INTENTION_IDLE);
        }

        if(getIntention() == Intention.INTENTION_ATTACK) {
            setIntention(Intention.INTENTION_ATTACK, attackTarget);
        }
    }

    @Override
    protected void onEvtAttacked(Entity attacker) {
        super.onEvtAttacked(attacker);

        if(getAttackTarget() != attacker) {
            setAttackTarget(attacker);
        }
        if(getTarget() != attacker) {
            setTarget(attacker);
        }
        if(getFollowTarget() != attacker) {
            setFollowTarget(attacker);
        }

        //TODO check range and set intention according
        // Stop moving if was patroling
        if(getIntention() == Intention.INTENTION_MOVE_TO && movingReason == EntityMovingReason.Walking) {
            GameTimeControllerService.getInstance().removeMovingObject(owner);
        }

        setIntention(Intention.INTENTION_ATTACK, attackTarget);
    }

    @Override
    protected void onEvtForgetObject(Entity object) {
        super.onEvtForgetObject(object);

        //TODO forget player if not attacked for a while
        //TODO forget player if player is too far
    }

    /*
    =========================
    ========= THINK =========
    =========================
     */
    void thinkIdle() {
        /* Check if npc needs to change its intention */
        if (npc.isRandomWalk() && shouldWalk()) {
            movingReason = EntityMovingReason.Walking;

            // Update npc move speed to its walking speed
            npc.getStatus().setMoveSpeed(npc.getTemplate().getBaseWalkSpd());
            randomWalk();
        }
    }

    void thinkAttack() {
        if(attackTarget == null || !owner.getKnownList().knowsObject(attackTarget) || attackTarget.isDead()) {
            if(Config.PRINT_AI_LOGS) {
                log.warn("[AI][{}] Attack target is null or dead", owner.getId());
            }
            //TODO: teleport to spawn if too far on next patrol
            notifyEvent(Event.CANCEL);
            clearTarget();
            setIntention(Intention.INTENTION_IDLE);
            return;
        }

        // If target too far follow target
        float attackRange = getOwner().getTemplate().baseAtkRange;
        if(VectorUtils.calcDistance2D(getOwner().getPos(), attackTarget.getPos()) > attackRange) {
            //TODO: Wait for attack to finish

            // Stop auto attacking
            notifyEvent(Event.CANCEL);

            if(Config.PRINT_AI_LOGS) {
                log.debug("[AI][{}] Start moving to attacker", owner.getId());
            }

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
        if(Config.PRINT_AI_LOGS) {
            log.debug("[AI][{}] Start attack", owner.getId());
        }
        owner.doAttack(attackTarget);
    }

    /*
    =========================
    ======= INTENTION =======
    =========================
     */
    @Override
    protected void onIntentionMoveTo(Point3D destination) {
        super.onIntentionMoveTo(destination);

        // Check if still running
        if (owner.moveTo(destination)) {
            return;
        }

        setIntention(Intention.INTENTION_IDLE);
    }

    @Override
    protected void onIntentionIdle() {
        super.onIntentionIdle();

        if (getIntention() == Intention.INTENTION_MOVE_TO) {
            getOwner().setMoving(false);
        }

        intention = Intention.INTENTION_IDLE;
    }

    /*
    =========================
    ========= OTHER =========
    =========================
     */
    private boolean shouldWalk() {
        Random r = new Random();
        if(r.nextInt(101) <=  Math.min((int) Config.AI_PATROL_CHANCE, 100)) {
            return true;
        }

        return false;
    }

    // default monster behaviour
    private void randomWalk() {
        if ((npc.getSpawnInfo() != null) && npc.isOnGeoData()) {
            try {
                Node n = Geodata.getInstance().findRandomNodeInRange(npc.getSpawnInfo().getSpawnPosition(), 6);
                setIntention(Intention.INTENTION_MOVE_TO, n.getCenter());
            } catch (Exception e) {
                if(Config.PRINT_PATHFINDER_LOGS) {
                    log.debug(e);
                }

                owner.setMoving(false);
                setIntention(Intention.INTENTION_IDLE);
            }
        }
    }
}
