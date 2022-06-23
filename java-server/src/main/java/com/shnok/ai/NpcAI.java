package com.shnok.ai;

import com.shnok.GameTimeController;
import com.shnok.Server;
import com.shnok.ThreadPoolManager;
import com.shnok.ai.enums.Intention;
import com.shnok.model.Point3D;
import com.shnok.model.entities.NpcInstance;
import com.shnok.pathfinding.Geodata;
import com.shnok.pathfinding.node.NodeType;
import com.shnok.serverpackets.ObjectAnimation;

import java.util.Random;
import java.util.concurrent.Future;

public class NpcAI extends BaseAI implements Runnable {
    private final int patrolRate = 1;
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

        NpcInstance npc = (NpcInstance) _owner;
        _thinking = true;

        /* Is NPC waiting ? */
        if (getIntention() == Intention.INTENTION_IDLE) {
            /* Check if npc needs to patrol */
            Random r = new Random();
            if ((npc.getSpawn() != null) && (r.nextInt(patrolRate) == 0) && npc.isOnGeoData()) {
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

        //System.out.println(getIntention());
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
            _aiTask.cancel(true);
            _aiTask = null;
        }
    }

    @Override
    protected void onEvtDead() {
        if (getIntention() == Intention.INTENTION_MOVE_TO) {
            GameTimeController.getInstance().removeMovingObject(_owner);
        }
        stopAITask();
    }

    @Override
    protected void onEvtArrived() {
        if (_owner.moveToNextRoutePoint()) {
            return;
        }

        if (getIntention() == Intention.INTENTION_MOVE_TO) {
            setIntention(Intention.INTENTION_IDLE);
        }
    }

    @Override
    protected void onIntentionMoveTo(Point3D pos) {
        _intention = Intention.INTENTION_MOVE_TO;

        if (_owner.canMove()) {
            if (_owner.moveTo((int) pos.getX(), (int) pos.getY(), (int) pos.getZ())) {
                _moving = true;
                return;
            }
        }

        setIntention(Intention.INTENTION_IDLE);
    }

    @Override
    protected void onIntentionIdle() {
        if (getIntention() == Intention.INTENTION_MOVE_TO) {
            _moving = false;

            // Send a packet to notify npc stop moving
            ObjectAnimation packet = new ObjectAnimation(_owner.getId(), (byte) 0, 0f);
            Server.getInstance().broadcastAll(packet);
        }

        _intention = Intention.INTENTION_IDLE;
    }
}
