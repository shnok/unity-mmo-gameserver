package com.shnok.javaserver.service;

import com.shnok.javaserver.model.SpawnInfo;
import com.shnok.javaserver.thread.SpawnThread;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class SpawnManagerService {
    @Autowired
    private DatabaseMockupService databaseMockupService;
    @Autowired
    private ThreadPoolManagerService threadPoolManagerService;

    private List<SpawnInfo> registeredSpawns;

    @Autowired
    private SpawnManagerService() {
        registeredSpawns = new ArrayList<>();
    }

    public void initialize() {
        log.info("Initializing spawner manager.");
        fillSpawnList();
        log.info("Loaded {} spawn point(s) from the database.", registeredSpawns.size());
        spawnMonsters();
        log.info("Spawning monsters.");
    }
    

    /* Later should load spawnlist from database */
    public void fillSpawnList() {
        registeredSpawns = databaseMockupService.getSpawnList();
    }

    public void spawnMonsters() {
        registeredSpawns.forEach((v) -> {
            threadPoolManagerService.scheduleSpawn(new SpawnThread(v), 0);
        });
    }

    public void respawn(int id) {
        SpawnInfo info = registeredSpawns.get(id);
        threadPoolManagerService.scheduleSpawn(new SpawnThread(info), info.getRespawnDelay());
    }
}
