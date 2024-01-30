package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.enums.EntityAnimation;
import com.shnok.javaserver.enums.EntityMovingReason;
import com.shnok.javaserver.pathfinding.node.Node;
import com.shnok.javaserver.service.GameTimeControllerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entity.Entity;
import com.shnok.javaserver.model.entity.NpcInstance;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.dto.serverpackets.ObjectAnimationPacket;
import lombok.extern.log4j.Log4j2;

import java.util.Random;
import java.util.concurrent.Future;

@Log4j2
public class NpcAI extends BaseAI implements Runnable {
//    private int patrolIndex = 0;
//    private int patrolDirection = 0;
    private NpcInstance npc;
    private Future<?> aiTask;
    private boolean thinking = false;

    public NpcAI() {
        startAITask();
    }

    @Override
    public void run() {
        onEvtThink();
    }

    @Override
    protected void onEvtThink() {
        if (thinking || owner == null) {
            return;
        }
        if (npc == null) {
            npc = (NpcInstance) owner;
        }

        thinking = true;

        /* Is NPC waiting ? */
        if (getIntention() == Intention.INTENTION_IDLE) {
            /* Check if npc needs to change its intention */
            if (npc.doPatrol() && npc.getPatrolWaypoints() != null) {
//                if (npc.getPatrolWaypoints().length > 0) {
//                    patrol();
//                }
            } else if (npc.doRandomWalk() && shouldWalk()) {
                movingReason = EntityMovingReason.Walking;

                // Update npc move speed to its walking speed
                npc.getStatus().setMoveSpeed(npc.getTemplate().getBaseWalkSpd());
                randomWalk();
            }
        }

        thinking = false;

        startAITask();
    }

    private boolean shouldWalk() {
        Random r = new Random();
        if(r.nextInt(100 - Math.min((int) Config.AI_PATROL_CHANCE, 100)) == 0) {
            return true;
        }

        return false;
    }

//    private void patrol() {
//        Point3D wayPoint = new Point3D();
//        if (patrolDirection == 0) {
//            if (patrolIndex < npc.getPatrolWaypoints().length - 1) {
//                wayPoint = npc.getPatrolWaypoints()[patrolIndex++];
//            } else {
//                patrolDirection = 1;
//                wayPoint = npc.getPatrolWaypoints()[patrolIndex--];
//            }
//        } else if (patrolDirection == 1) {
//            if (patrolIndex > 0) {
//                wayPoint = npc.getPatrolWaypoints()[patrolIndex--];
//            } else {
//                patrolDirection = 0;
//                wayPoint = npc.getPatrolWaypoints()[patrolIndex++];
//            }
//        }
//
//        setIntention(Intention.INTENTION_MOVE_TO, wayPoint);
//    }

    // default monster behaviour
    private void randomWalk() {
        if ((npc.getSpawnInfo() != null) && npc.isOnGeoData()) {
            try {
                Node n = Geodata.getInstance().findRandomNodeInRange(npc.getSpawnInfo().getSpawnPosition(), 6);

                ObjectAnimationPacket packet = new ObjectAnimationPacket(
                        npc.getId(), EntityAnimation.Walk.getValue(), 1f);
                npc.broadcastPacket(packet);

                setIntention(Intention.INTENTION_MOVE_TO, n.getCenter());
            } catch (Exception e) {
                log.debug(e);
            }
        }
    }

    private void startAITask() {
        if (aiTask == null) {
            aiTask = ThreadPoolManagerService.getInstance().scheduleAiAtFixedRate(this, 1000,
                    Config.AI_LOOP_RATE_MS);
        }
    }

    public void stopAITask() {
        if (aiTask != null) {
            aiTask.cancel(true);
            aiTask = null;
        }
    }

    @Override
    protected void onEvtDead() {
        if (getIntention() == Intention.INTENTION_MOVE_TO) {
            GameTimeControllerService.getInstance().removeMovingObject(owner);
        }
        stopAITask();
    }

    @Override
    protected void onEvtArrived() {
        if (owner.moveToNextRoutePoint()) {
            return;
        }

        if (getIntention() == Intention.INTENTION_MOVE_TO) {
            setIntention(Intention.INTENTION_IDLE);
        }
    }

    @Override
    public void setOwner(Entity owner) {
        this.owner = owner;
    }

    @Override
    protected void onIntentionMoveTo(Point3D destination) {
        intention = Intention.INTENTION_MOVE_TO;

        if (owner.canMove()) {
            if (owner.moveTo(destination)) {
                moving = true;
                return;
            }
        }

        setIntention(Intention.INTENTION_IDLE);
    }

    @Override
    protected void onIntentionIdle() {
        if (getIntention() == Intention.INTENTION_MOVE_TO) {
            moving = false;
            ObjectAnimationPacket packet = new ObjectAnimationPacket(npc.getId(), EntityAnimation.Wait.getValue(), 1f);
            npc.broadcastPacket(packet);
        }

        intention = Intention.INTENTION_IDLE;
    }
}
