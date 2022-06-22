package com.shnok.ai;

import com.shnok.ThreadPoolManager;
import com.shnok.ai.enums.Intention;
import com.shnok.model.Point3D;
import com.shnok.model.entities.NpcInstance;
import com.shnok.pathfinding.Geodata;
import com.shnok.pathfinding.node.NodeType;

import java.util.Random;
import java.util.concurrent.Future;

public class NpcAI extends BaseAI implements Runnable {
    private Future<?> _aiTask;
    private boolean _thinking = false;

    public NpcAI() {
        startAITask();
    }

    @Override
    public void run() {
        onEvtThink();
    }

    @Override
    protected void onEvtThink() {
        if (_thinking || _owner == null) {
            return;
        }
        System.out.println("Think");

        NpcInstance npc = (NpcInstance) _owner;
        _thinking = true;

        /* Is NPC waiting ? */
        if (getIntention() == Intention.INTENTION_IDLE) {
            /* Check if npc needs to patrol */
            Random r = new Random();
            if ((npc.getSpawn() != null) && (r.nextInt(5) == 0) && npc.isOnGeoData()) {
                int x1, y1, z1;
                int maxDriftRange = 5;

                for (int i = 0; i < 5; i++) {
                    x1 = ((int) npc.getSpawn().getSpawnPos().getX() + r.nextInt(maxDriftRange * 2)) - maxDriftRange;
                    y1 = 0;
                    z1 = ((int) npc.getSpawn().getSpawnPos().getZ() + r.nextInt(maxDriftRange * 2)) - maxDriftRange;

                    Point3D pos = Geodata.getInstance().clampToWorld(new Point3D(x1, y1, z1));
                    if (Geodata.getInstance().getNodeType((int) pos.getX(), (int) pos.getY(), (int) pos.getZ()) == NodeType.WALKABLE) {
                        setIntention(Intention.INTENTION_MOVE_TO, new Point3D(x1, y1, z1));
                        break;
                    }
                }
            }
        }

        _thinking = false;
        startAITask();
    }


    private void startAITask() {
        if (_aiTask == null) {
            _aiTask = ThreadPoolManager.getInstance().scheduleAiAtFixedRate(this, 1000, 1000);
        }
    }

    private void stopAITask() {
        if (_aiTask != null) {
            _aiTask.cancel(false);
            _aiTask = null;
        }
    }

    @Override
    protected void onEvtDead() {
        stopAITask();
    }

    @Override
    protected void onEvtArrived() {
        System.out.println("Arrived");
        if (_owner.moveToNextRoutePoint()) {
            return;
        }

        if (getIntention() == Intention.INTENTION_MOVE_TO) {
            setIntention(Intention.INTENTION_IDLE);
        }
    }

    @Override
    protected void onIntentionMoveTo(Point3D pos) {
        moveTo((int) pos.getX(), (int) pos.getY(), (int) pos.getZ());
    }

    @Override
    protected void onIntentionIdle() {
        if (getIntention() == Intention.INTENTION_MOVE_TO) {
            stopMoving();
        }
    }

    private void moveTo(int x, int y, int z) {
        if (_owner.canMove()) {
            if (_owner.moveTo(x, y, z)) {
                _moving = true;
                // Send a packet to notify npc moving
            }
        }
    }

    private void stopMoving() {
        _moving = false;
        // Send a packet to notify npc stop moving
    }
}
