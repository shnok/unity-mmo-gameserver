package com.shnok.javaserver.service;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.db.entity.Npc;
import com.shnok.javaserver.db.entity.SpawnList;
import com.shnok.javaserver.db.repository.NpcRepository;
import com.shnok.javaserver.db.repository.SpawnListRepository;
import com.shnok.javaserver.model.template.NpcTemplate;
import com.shnok.javaserver.thread.SpawnThread;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.List;

@Log4j2
public class SpawnManagerService {
    private static SpawnManagerService instance;
    public static SpawnManagerService getInstance() {
        if (instance == null) {
            instance = new SpawnManagerService();
        }
        return instance;
    }

    private List<SpawnList> registeredSpawns;

    private SpawnManagerService() {
        registeredSpawns = new ArrayList<>();
    }

    public void initialize() {
        log.info("Initializing spawner manager.");
        loadSpawnList();
        log.info("Loaded {} spawn point(s) from the database.", registeredSpawns.size());
        spawnMonsters();
        log.info("Spawning monsters.");
    }
    

    /* Later should load spawnlist from database */
    public void loadSpawnList() {
        SpawnListRepository spawnListRepository = new SpawnListRepository();
        if(Config.SPAWN_NPCS) {
            registeredSpawns = spawnListRepository.getAllSpawnList();
        }
    }

    public void spawnMonsters() {
        registeredSpawns.forEach((spawnInfo) -> {
            NpcRepository npcRepository = new NpcRepository();
            Npc npc = npcRepository.getNpcById(spawnInfo.getNpcId());
            if(npc != null) {
                NpcTemplate npcTemplate = new NpcTemplate(npc);
                ThreadPoolManagerService.getInstance().scheduleSpawn(new SpawnThread(spawnInfo, npcTemplate), 0);
            } else {
                log.error("Could not find npc with id {} in the database.", spawnInfo.getNpcId());
            }
        });
    }

    public void respawn(SpawnList spawnInfo, NpcTemplate template) {
        ThreadPoolManagerService.getInstance().scheduleSpawn(new SpawnThread(spawnInfo, template), spawnInfo.getRespawnDelay());
    }
}
