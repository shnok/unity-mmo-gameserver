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
            registeredSpawns.addAll(spawnListRepository.getAllNPCs());
        }

        if(Config.SPAWN_MONSTERS) {
            registeredSpawns.addAll(spawnListRepository.getAllMonsters());
        }

        if(Config.SPAWN_DEBUG) {
            // For debug purposes
            registeredSpawns.add(new SpawnList(
                    25405,
                    "gludio32_1725_18",
                    1,
                    20442,
                    4502.61f, 	-70.78f, -1717.26f,
                    0f,
                    0f,
                    210.01f,
                    3000,
                    0,
                    0));
            registeredSpawns.add(new SpawnList(
                    25406,
                    "gludio32_1725_18",
                    1,
                    20442,
                    4533.592f,-71, -1625.774f,
                    0f,
                    0f,
                    210.01f,
                    3000,
                    0,
                    0));
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
        log.debug("Scheduling respawn for [{}]{} in {}ms",
                spawnInfo.getNpcId(),template.getName(), spawnInfo.getRespawnDelay());
        ThreadPoolManagerService.getInstance().scheduleSpawn(
                new SpawnThread(spawnInfo, template), spawnInfo.getRespawnDelay());
    }
}
