package com.shnok.javaserver.thread;

import com.shnok.javaserver.db.entity.Npc;
import com.shnok.javaserver.db.entity.SpawnList;
import com.shnok.javaserver.model.status.NpcStatus;
import com.shnok.javaserver.model.template.NpcTemplate;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.thread.ai.NpcAI;
import com.shnok.javaserver.model.entity.NpcInstance;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SpawnThread implements Runnable {
    private final SpawnList spawnInfo;
    private final NpcTemplate npcTemplate;

    public SpawnThread(SpawnList spawnInfo, NpcTemplate npcTemplate) {
        this.spawnInfo = spawnInfo;
        this.npcTemplate = npcTemplate;
    }

    @Override
    public void run() {
        try {
            /*if (oldSpawnInfo.isRandomSpawn()) {
                Point3D randomPos = Geodata.getInstance().randomLocation();
                oldSpawnInfo.setSpawnPos(new Point3D(randomPos.getX() + 0.5f, randomPos.getY(), randomPos.getZ() + 0.5f));
            }*/

            if (npcTemplate == null) {
                log.info("Npc template is null, skipping respawn.");
                return;
            }

            NpcInstance npc = new NpcInstance(WorldManagerService.getInstance().nextID(), npcTemplate);
            npc.setPosition(spawnInfo.getSpawnPosition());
            npc.setHeading(spawnInfo.getHeading());
            npc.setSpawn(spawnInfo);

            if (!npc.isStatic()) {
                NpcAI ai = new NpcAI();
                ai.setOwner(npc);
                npc.attachAI(ai);
            }

            WorldManagerService.getInstance().addNPC(npc);
            //ServerService.getInstance().broadcast(new NpcInfoPacket(npc));

            log.debug("Spawned monster {} with id {} at {}.", npc.getTemplate().getNpcId(), npc.getId(),
                    spawnInfo.getSpawnPosition().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
