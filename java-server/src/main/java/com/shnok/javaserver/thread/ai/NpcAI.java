package com.shnok.javaserver.thread.ai;

import com.shnok.javaserver.service.GameTimeControllerService;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.enums.Intention;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entities.Entity;
import com.shnok.javaserver.model.entities.NpcInstance;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.node.NodeType;
import com.shnok.javaserver.dto.serverpackets.ObjectAnimation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;
import java.util.concurrent.Future;

public class NpcAI extends BaseAI implements Runnable {
    @Autowired
    private GameTimeControllerService gameTimeControllerService;
    @Autowired
    private ThreadPoolManagerService threadPoolManagerService;

    private final int randomWalkRate = 5;
    private int patrolIndex = 0;
    private int patrolDirection = 0;
    private NpcInstance npc;
    private Future<?> aiTask;
    private boolean thinking = false;

    @Autowired
    ServerService serverService;

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
            /* Check if npc needs to random walk */
            if (npc.doPatrol() && npc.getPatrolWaypoints() != null) {
                if (npc.getPatrolWaypoints().length > 0) {
                    patrol();
                }
            } else if (npc.doRandomWalk()) {
                randomWalk();
            }
        }

        //System.out.println(getIntention());
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

    private void randomWalk() {
        Random r = new Random();
        if ((npc.getSpawn() != null) && (r.nextInt(randomWalkRate) == 0) && npc.isOnGeoData()) {
            int x1, y1, z1;
            int maxDriftRange = 5;

            for (int i = 0; i < 5; i++) {
                x1 = ((int) npc.getSpawn().getSpawnPos().getX() + r.nextInt(maxDriftRange * 2)) - maxDriftRange;
                y1 = (int) npc.getSpawn().getSpawnPos().getY();
                z1 = ((int) npc.getSpawn().getSpawnPos().getZ() + r.nextInt(maxDriftRange * 2)) - maxDriftRange;

                Point3D pos = Geodata.getInstance().clampToWorld(new Point3D(x1, y1, z1));
                if (Geodata.getInstance().getNodeType((int) pos.getX(), (int) pos.getY(), (int) pos.getZ()) == NodeType.WALKABLE) {
                    setIntention(Intention.INTENTION_MOVE_TO, new Point3D(x1, y1, z1));
                    break;
                }
            }
        }
    }

    private void startAITask() {
        if (aiTask == null) {
            aiTask = threadPoolManagerService.scheduleAiAtFixedRate(this, 1000, 1000);
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
            gameTimeControllerService.removeMovingObject(owner);
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
        owner = owner;
    }

    @Override
    protected void onIntentionMoveTo(Point3D pos) {
        intention = Intention.INTENTION_MOVE_TO;

        if (owner.canMove()) {
            if (owner.moveTo((int) pos.getX(), (int) pos.getY(), (int) pos.getZ())) {
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
            ObjectAnimation packet = new ObjectAnimation(owner.getId(), (byte) 0, 0f);
            serverService.broadcastAll(packet);
        }

        intention = Intention.INTENTION_IDLE;
    }
}
