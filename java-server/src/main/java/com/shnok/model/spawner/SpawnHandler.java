package com.shnok.model.spawner;

import com.shnok.ThreadPoolManager;
import com.shnok.World;
import com.shnok.model.Point3D;

import java.util.HashMap;
import java.util.Map;

public class SpawnHandler {

    private Map<Integer, SpawnInfo> _registeredSpawns;
    private static SpawnHandler _instance;

    public static SpawnHandler getInstance() {
        if (_instance == null) {
            _instance = new SpawnHandler();
        }

        return _instance;
    }

    private SpawnHandler() {
        _registeredSpawns = new HashMap<>();
    }

    /* Later should load spawnlist from database */
    public void fillSpawnList() {
        /*for(int i = 0; i < 5; i++) {
            SpawnInfo info = new SpawnInfo(World.getInstance().nextID(), 0, 1000);
            _registeredSpawns.put(info.getObjectId(), info);
        }*/

        SpawnInfo info = new SpawnInfo(World.getInstance().nextID(), 0, 1000, new Point3D(3, 0, 3));
        _registeredSpawns.put(info.getObjectId(), info);
    }

    public void spawnMonsters() {
        _registeredSpawns.forEach((k, v) -> {
            ThreadPoolManager.getInstance().scheduleSpawn(new SpawnThread(v), 0);
        });
    }

    public void respawn(int id) {
        SpawnInfo info = _registeredSpawns.get(id);
        ThreadPoolManager.getInstance().scheduleSpawn(new SpawnThread(info), info.getRespawnDelay());
    }

    public Map<Integer, SpawnInfo> getRegisteredSpawns() {
        return _registeredSpawns;
    }
}
