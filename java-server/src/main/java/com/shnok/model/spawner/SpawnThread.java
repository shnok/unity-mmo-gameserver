package com.shnok.model.spawner;

import com.shnok.Server;
import com.shnok.World;
import com.shnok.ai.NpcAI;
import com.shnok.model.FakeDatabase;
import com.shnok.model.Point3D;
import com.shnok.model.entities.NpcInstance;
import com.shnok.pathfinding.Geodata;
import com.shnok.serverpackets.NpcInfo;

public class SpawnThread implements Runnable {

    private final SpawnInfo _spawnInfo;

    public SpawnThread(SpawnInfo spawnInfo) {
        _spawnInfo = spawnInfo;
    }

    @Override
    public void run() {
        try {
            if (_spawnInfo.isRandomSpawn()) {
                Point3D randomPos = Geodata.getInstance().randomLocation();
                _spawnInfo.setSpawnPos(new Point3D(randomPos.getX() + 0.5f, randomPos.getY(), randomPos.getZ() + 0.5f));
            }

            NpcInstance npc = FakeDatabase.getInstance().getNpc(_spawnInfo.getNpcId());
            if (npc == null) {
                return;
            }

            npc.setId(World.getInstance().nextID());
            npc.setPosition(_spawnInfo.getSpawnPos());
            npc.setSpawn(_spawnInfo);

            if (!npc.isStatic()) {
                NpcAI ai = new NpcAI();
                ai.setOwner(npc);
                npc.attachAI(ai);
            }

            World.getInstance().addNPC(npc);
            Server.getInstance().broadcastAll(new NpcInfo(npc));

            System.out.println("Spawned monster at " + _spawnInfo.getSpawnPos().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
