package com.shnok.javaserver.db.service;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.OldSpawnInfo;
import com.shnok.javaserver.model.entity.Entity;
import com.shnok.javaserver.model.entity.NpcInstance;
import com.shnok.javaserver.model.entity.PlayerInstance;
import com.shnok.javaserver.model.status.NpcStatus;
import com.shnok.javaserver.model.status.PlayerStatus;
import javolution.util.FastMap;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class DatabaseMockupService {
    private final FastMap<Integer, Entity> npcList = new FastMap<>();
    private final List<OldSpawnInfo> spawnList = new ArrayList<>();
    private Point3D playerSpawnPoint;

    private static DatabaseMockupService instance;
    public static DatabaseMockupService getInstance() {
        if (instance == null) {
            instance = new DatabaseMockupService();
        }
        return instance;
    }

    public void initialize() {
        log.info("Initializing mockup database.");
        playerSpawnPoint = Config.PLAYER_SPAWN_POINT;
        generateNpcList();
        generateSpawnList();
        log.info("Generated {} npc(s)", npcList.size());
        log.info("Generated {} spawn points(s)", spawnList.size());
    }

    private void generateNpcList() {
        NpcStatus status = new NpcStatus(1, 3);
        npcList.put(0, new NpcInstance(0, status, true, false, false, null));
        npcList.put(1, new NpcInstance(1, status, false, true, false, null));
        npcList.put(2, new NpcInstance(2, status, false, false, true, new Point3D[]{
                new Point3D(-5, 0, 5),
                new Point3D(-5, 0, -5),
                new Point3D(5, 0, -5),
                new Point3D(5, 0, 5)
        }));
        npcList.put(3, new NpcInstance(3, status, false, false, true, new Point3D[]{
                new Point3D(-13, 4, 12),
                new Point3D(9, 1, -14)
        }));
    }

    private void generateSpawnList() {
        spawnList.add(new OldSpawnInfo(spawnList.size(), 1, 1000, new Point3D(4627.25f, -71.414f, -1644.323f)));
        spawnList.add(new OldSpawnInfo(spawnList.size(), 1, 1000, new Point3D(4627.25f, -71.414f, -1647.323f)));
        spawnList.add(new OldSpawnInfo(spawnList.size(), 1, 1000, new Point3D(4628.25f, -71.414f, -1647.323f)));
        spawnList.add(new OldSpawnInfo(spawnList.size(), 1, 1000, new Point3D(4630.25f, -71.414f, -1645.323f)));
    }

    public NpcInstance getNpc(int id) {
        if (npcList.containsKey(id)) {
            NpcInstance n = (NpcInstance) npcList.get(id);
            NpcStatus s = n.getStatus();
            return new NpcInstance(
                    n.getNpcId(),
                    new NpcStatus(s.getLevel(),
                            s.getMaxHp()),
                    n.isStatic(),
                    n.doRandomWalk(),
                    n.doPatrol(),
                    n.getPatrolWaypoints());
        }
        return null;
    }

    public PlayerInstance getPlayerData(String username) {
        PlayerInstance player = new PlayerInstance(0, username);
        player.setStatus(new PlayerStatus());
        player.setPosition(playerSpawnPoint);
        return player;
    }

    public List<OldSpawnInfo> getSpawnList() {
        return spawnList;
    }

}
