package com.shnok.model.spawner;

import com.shnok.ThreadPoolManager;
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

    public void FillSpawnList(SpawnInfo spawn) {
        _registeredSpawns.put(0, new SpawnInfo(0, 0, 1000));
        ThreadPoolManager.getInstance().scheduleSpawn(new SpawnThread(0), 1000);
    }

    public Map<Integer, SpawnInfo> getRegisteredSpawns() {
        return _registeredSpawns;
    }
}
