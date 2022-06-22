package com.shnok.ai;

import com.shnok.ThreadPoolManager;
import com.shnok.model.entities.NpcInstance;
import com.shnok.pathfinding.PathFinding;

import java.util.Random;
import java.util.concurrent.Future;

public class MonsterAI extends BaseAI implements Runnable {
    private Future<?> _aiTask;
    private boolean _thinking = false;
    public MonsterAI() {
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
        System.out.println("Think");
        _thinking = true;

        /* Check if need to patrol */
        Random r = new Random();
        if ((npc.getSpawn() != null) && (r.nextInt(2) == 0) && !_moving) {
            int x1, y1, z1;
            int maxDriftRange = 5;

            x1 = ((int)npc.getSpawn().getSpawnPos().getX() + r.nextInt(maxDriftRange * 2)) - maxDriftRange;
            y1 = 0;
            z1 = ((int)npc.getSpawn().getSpawnPos().getZ() + r.nextInt(maxDriftRange * 2)) - maxDriftRange;

            moveTo(x1, y1, z1);
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
        stopMove();
    }
}
