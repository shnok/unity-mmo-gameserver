package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.pathfinding.node.Node;
import com.shnok.javaserver.service.GameTimeControllerService;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entity.Entity;
import com.shnok.javaserver.model.entity.NpcInstance;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.node.NodeType;
import com.shnok.javaserver.dto.serverpackets.ObjectAnimationPacket;
import lombok.extern.log4j.Log4j2;

import java.util.Random;
import java.util.concurrent.Future;

@Log4j2
public class NpcAI extends BaseAI implements Runnable {
    private final int randomWalkRate = 5;
    private int patrolIndex = 0;
    private int patrolDirection = 0;
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
                if (npc.getPatrolWaypoints().length > 0) {
                    patrol();
                }
            } else if (npc.doRandomWalk()) {
                randomWalk();
            }
        }

        thinking = false;
        startAITask();
    }

    private void patrol() {
        Point3D wayPoint = new Point3D();
        if (patrolDirection == 0) {
            if (patrolIndex < npc.getPatrolWaypoints().length - 1) {
                wayPoint = npc.getPatrolWaypoints()[patrolIndex++];
            } else {
                patrolDirection = 1;
                wayPoint = npc.getPatrolWaypoints()[patrolIndex--];
            }
        } else if (patrolDirection == 1) {
            if (patrolIndex > 0) {
                wayPoint = npc.getPatrolWaypoints()[patrolIndex--];
            } else {
                patrolDirection = 0;
                wayPoint = npc.getPatrolWaypoints()[patrolIndex++];
            }
        }

        setIntention(Intention.INTENTION_MOVE_TO, wayPoint);
    }

    // default monster behaviour
    private void randomWalk() {
        Random r = new Random();
        if ((npc.getSpawn() != null) && (r.nextInt(randomWalkRate) == 0) && npc.isOnGeoData()) {
            try {
                Node n = Geodata.getInstance().findRandomNodeInRange(npc.getPos(), 6);
                log.debug("New random pos: " + n.getCenter());
                setIntention(Intention.INTENTION_MOVE_TO, n.getCenter());
            } catch (Exception e) {
                log.debug(e);
            }

        }
    }

    private void startAITask() {
        if (aiTask == null) {
            aiTask = ThreadPoolManagerService.getInstance().scheduleAiAtFixedRate(this, 1000, 1000);
        }
    }

    private void stopAITask() {
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

            // Send a packet to notify npc stop moving
            ObjectAnimationPacket packet = new ObjectAnimationPacket(owner.getId(), (byte) 0, 0f);
            ServerService.getInstance().broadcast(packet);
        }

        intention = Intention.INTENTION_IDLE;
    }
}
