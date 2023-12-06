package com.shnok.javaserver.thread;

import com.shnok.javaserver.db.entity.SpawnList;
import com.shnok.javaserver.model.OldSpawnInfo;
import com.shnok.javaserver.model.status.NpcStatus;
import com.shnok.javaserver.service.ServerService;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.thread.ai.NpcAI;
import com.shnok.javaserver.db.service.DatabaseMockupService;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entity.NpcInstance;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.dto.serverpackets.NpcInfoPacket;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SpawnThread implements Runnable {
    private final SpawnList spawnInfo;

    public SpawnThread(SpawnList spawnInfo) {
        this.spawnInfo = spawnInfo;
    }

    @Override
    public void run() {
        try {
            /*if (oldSpawnInfo.isRandomSpawn()) {
                Point3D randomPos = Geodata.getInstance().randomLocation();
                oldSpawnInfo.setSpawnPos(new Point3D(randomPos.getX() + 0.5f, randomPos.getY(), randomPos.getZ() + 0.5f));
            }*/

            NpcInstance npc = DatabaseMockupService.getInstance().getNpc(spawnInfo.getNpcId());
            if (npc == null) {
                log.info("Did not find npc {}. Spawning a basic npc.", spawnInfo.getNpcId());
                npc = new NpcInstance(0, new NpcStatus(1, 3), true, false, false, null);
            }

            npc.setId(WorldManagerService.getInstance().nextID());
            npc.setPosition(spawnInfo.getSpawnPosition());
            npc.setSpawn(spawnInfo);

            if (!npc.isStatic()) {
                NpcAI ai = new NpcAI();
                ai.setOwner(npc);
                npc.attachAI(ai);
            }

            WorldManagerService.getInstance().addNPC(npc);
            ServerService.getInstance().broadcast(new NpcInfoPacket(npc));

            log.debug("Spawned monster {} with id {} at {}.", npc.getNpcId(), npc.getId(), spawnInfo.getSpawnPosition().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
