package com.shnok.model.spawner;

import com.shnok.Server;
import com.shnok.World;
import com.shnok.model.Point3D;
import com.shnok.model.status.NpcStatus;
import com.shnok.model.status.Status;
import com.shnok.model.entities.NpcInstance;
import com.shnok.serverpackets.NpcInfo;
import com.shnok.serverpackets.RemoveObject;

public class SpawnThread implements Runnable {

    private SpawnInfo _spawnInfo;

    public SpawnThread(SpawnInfo spawnInfo) {
        _spawnInfo = spawnInfo;
    }

    @Override
    public void run() {
        try {
            if(_spawnInfo.isRandomSpawn()) {
                float randomX = (float)(Math.random()*(10f+1)-5f);
                float randomZ = (float)(Math.random()*(10f+1)-5f);
                Point3D randomPos = new Point3D(randomX, 0, randomZ);
                _spawnInfo.setSpawnPos(randomPos);
            }

            _spawnInfo.setSpawned(true);
            NpcInstance npc = new NpcInstance(_spawnInfo.getObjectId(), _spawnInfo.getNpcId());
            npc.setStatus(new NpcStatus(1, 3));
            npc.setPosition(_spawnInfo.getSpawnPos());
            World.getInstance().addNPC(npc);
            Server.getInstance().broadcastAll(new NpcInfo(npc));
            System.out.println("Spawned monster at " + _spawnInfo.getSpawnPos().toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
