package com.shnok.javaserver.service;

import com.shnok.javaserver.db.entity.SpawnList;
import com.shnok.javaserver.db.interfaces.SpawnListDao;
import com.shnok.javaserver.db.repository.SpawnListRepository;
import com.shnok.javaserver.db.service.DatabaseMockupService;
import com.shnok.javaserver.model.OldSpawnInfo;
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
        registeredSpawns = spawnListRepository.getAllSpawnList();
    }

    public void spawnMonsters() {
        registeredSpawns.forEach((v) -> {
            ThreadPoolManagerService.getInstance().scheduleSpawn(new SpawnThread(v), 0);
        });
    }

    public void respawn(int id) {
        SpawnList info = registeredSpawns.get(id);
        ThreadPoolManagerService.getInstance().scheduleSpawn(new SpawnThread(info), info.getRespawnDelay());
    }
}
