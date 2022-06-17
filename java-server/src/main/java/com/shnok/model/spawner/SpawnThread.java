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
            World.getInstance().addNPC(npc);
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
