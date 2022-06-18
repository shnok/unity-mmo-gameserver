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

    public void FillSpawnList() {
        for(int i = 0; i < 5; i++) {
            float randomX = (float)(Math.random()*(10f+1)-5f);
            float randomZ = (float)(Math.random()*(10f+1)-5f);
            Point3D randomPos = new Point3D(randomX, 0, randomZ);
            SpawnInfo info = new SpawnInfo(World.getInstance().nextID(), 0, 1000, randomPos);
            _registeredSpawns.put(info.getObjectId(), info);
        }
    }

    public void SpawnMonsters() {
        _registeredSpawns.forEach((k, v) -> {
            ThreadPoolManager.getInstance().scheduleSpawn(new SpawnThread(v.getObjectId()), 0);
        });
    }

    public Map<Integer, SpawnInfo> getRegisteredSpawns() {
        return _registeredSpawns;
    }
}
