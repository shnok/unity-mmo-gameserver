package com.shnok.javaserver.thread;

import com.shnok.javaserver.model.SpawnInfo;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.thread.ai.NpcAI;
import com.shnok.javaserver.service.DatabaseMockupService;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entities.NpcInstance;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.dto.serverpackets.NpcInfo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;

@Log4j2
public class SpawnThread implements Runnable {
    @Autowired
    private WorldManagerService worldManagerService;
    @Autowired
    private DatabaseMockupService databaseMockupService;
    @Autowired
    private ServerService serverService;
    
    private final SpawnInfo spawnInfo;

    public SpawnThread(SpawnInfo spawnInfo) {
        this.spawnInfo = spawnInfo;
    }

    @Override
    public void run() {
        try {
            if (spawnInfo.isRandomSpawn()) {
                Point3D randomPos = Geodata.getInstance().randomLocation();
                spawnInfo.setSpawnPos(new Point3D(randomPos.getX() + 0.5f, randomPos.getY(), randomPos.getZ() + 0.5f));
            }

            NpcInstance npc = databaseMockupService.getNpc(spawnInfo.getNpcId());
            if (npc == null) {
                return;
            }

            npc.setId(worldManagerService.nextID());
            npc.setPosition(spawnInfo.getSpawnPos());
            npc.setSpawn(spawnInfo);

            if (!npc.isStatic()) {
                NpcAI ai = new NpcAI();
                ai.setOwner(npc);
                npc.attachAI(ai);
            }

            worldManagerService.addNPC(npc);
            serverService.broadcastAll(new NpcInfo(npc));

            log.debug("Spawned monster {} with id {} at {}.", npc.getNpcId(), npc.getId(), spawnInfo.getSpawnPos().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
