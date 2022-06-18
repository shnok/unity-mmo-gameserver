package com.shnok.model.spawner;

import com.shnok.World;
import com.shnok.model.entities.NpcInstance;

public class SpawnThread implements Runnable {

    private int _objectId;

    public SpawnThread(int objectId) {
        _objectId = objectId;
    }

    @Override
    public void run() {
        try {
            SpawnInfo spawnInfo = SpawnHandler.getInstance().getRegisteredSpawns().get(_objectId);
            spawnInfo.setSpawned(true);
            NpcInstance npc = new NpcInstance(spawnInfo.getObjectId(), spawnInfo.getNpcId());
            npc.setPosition(spawnInfo.getSpawnPos());
            World.getInstance().addNPC(npc);
            System.out.println("Spawned monster at " + spawnInfo.getSpawnPos().toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
