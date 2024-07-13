package com.shnok.javaserver.thread;

import com.shnok.javaserver.db.entity.DBSpawnList;
import com.shnok.javaserver.enums.NpcType;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.object.entity.NpcInstance;
import com.shnok.javaserver.model.template.NpcTemplate;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.node.Node;
import com.shnok.javaserver.service.WorldManagerService;
import lombok.extern.log4j.Log4j2;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
public class SpawnThread implements Runnable {
    private final DBSpawnList spawnInfo;
    private final NpcTemplate npcTemplate;

    public SpawnThread(DBSpawnList spawnInfo, NpcTemplate npcTemplate) {
        this.spawnInfo = spawnInfo;
        this.npcTemplate = npcTemplate;

        // adjust spawn info to node y position
        try {
            Node n = Geodata.getInstance().getClosestNodeAt(new Point3D(
                            spawnInfo.getSpawnPosition().getX(),
                            0,
                            spawnInfo.getSpawnPosition().getZ()));

            spawnInfo.setSpawnPosition(n.getCenter());
            log.debug("Adjusted spawninfo to position: {}", spawnInfo.getSpawnPosition());
        } catch (Exception ignored) {
        }
    }

    @Override
    public void run() {
        try {
            if (npcTemplate == null) {
                log.warn("Npc template is null, skipping respawn.");
                return;
            }

            NpcInstance npc = new NpcInstance(WorldManagerService.getInstance().nextID(), npcTemplate);
            npc.setCurrentHpMp(npc.getMaxHp(), npc.getMaxMp());

            if(npc.getTemplate().getType() == NpcType.L2Monster) {
                npc.setStatic(false);
                npc.setRandomWalk(true);
            } else {
                npc.setStatic(true);
                npc.setRandomWalk(false);
            }

            npc.setPosition(spawnInfo.getSpawnPosition());
            npc.setHeading(spawnInfo.getHeading());
            npc.setSpawnInfo(spawnInfo);

            if(server.aiKeepAlive()) {
                npc.refreshAI();
            }

            WorldManagerService.getInstance().addNPC(npc);
            //ServerService.getInstance().broadcast(new NpcInfoPacket(npc));

            log.debug("Spawned [{}][{}] with id {} at {}.",
                    npc.getTemplate().getNpcId(), npc.getTemplate().getName(), npc.getId(),
                    spawnInfo.getSpawnPosition().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
