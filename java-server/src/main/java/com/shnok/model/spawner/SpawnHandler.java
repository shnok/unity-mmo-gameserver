package com.shnok.model.spawner;

import com.shnok.ThreadPoolManager;
import com.shnok.model.FakeDatabase;

import java.util.ArrayList;
import java.util.List;

public class SpawnHandler {

    private static SpawnHandler _instance;
    private List<SpawnInfo> _registeredSpawns;

    private SpawnHandler() {
        _registeredSpawns = new ArrayList<>();
    }

    public static SpawnHandler getInstance() {
        if (_instance == null) {
            _instance = new SpawnHandler();
        }

        return _instance;
    }

    /* Later should load spawnlist from database */
    public void fillSpawnList() {
        _registeredSpawns = FakeDatabase.getInstance().getSpawnList();
    }

    public void spawnMonsters() {
        _registeredSpawns.forEach((v) -> {
            ThreadPoolManager.getInstance().scheduleSpawn(new SpawnThread(v), 0);
        });
    }

    public void respawn(int id) {
        SpawnInfo info = _registeredSpawns.get(id);
        ThreadPoolManager.getInstance().scheduleSpawn(new SpawnThread(info), info.getRespawnDelay());
    }
}
