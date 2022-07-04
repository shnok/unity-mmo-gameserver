package com.shnok.model;

import com.shnok.model.entities.Entity;
import com.shnok.model.entities.NpcInstance;
import com.shnok.model.spawner.SpawnInfo;
import com.shnok.model.status.NpcStatus;
import javolution.util.FastMap;

import java.util.ArrayList;
import java.util.List;

public class FakeDatabase {
    private static FakeDatabase _instance;
    private final FastMap<Integer, Entity> _npcList = new FastMap<>();
    private final List<SpawnInfo> _spawnList = new ArrayList<>();

    public FakeDatabase() {
        generateFakeData();
    }

    public static FakeDatabase getInstance() {
        if (_instance == null) {
            _instance = new FakeDatabase();
        }
        return _instance;
    }

    private void generateFakeData() {
        generateNpcList();
        generateSpawnList();
    }

    private void generateNpcList() {
        NpcStatus status = new NpcStatus(1, 3);
        _npcList.put(0, new NpcInstance(0, status, true, false, false, null));
        _npcList.put(1, new NpcInstance(1, status, false, true, false, null));
        _npcList.put(2, new NpcInstance(2, status, false, false, true, new Point3D[]{
                new Point3D(-5, 0, 5),
                new Point3D(-5, 0, -5),
                new Point3D(5, 0, -5),
                new Point3D(5, 0, 5)
        }));
        _npcList.put(3, new NpcInstance(3, status, false, false, true, new Point3D[]{
                new Point3D(6, 2, 6),
                new Point3D(6, 0, 6)
        }));
    }

    private void generateSpawnList() {
        for (int i = 0; i < 5; i++) {
            _spawnList.add(new SpawnInfo(_spawnList.size(), 0, 1000));
        }
        _spawnList.add(new SpawnInfo(_spawnList.size(), 1, 5000, new Point3D(-5, 2, 6)));
        _spawnList.add(new SpawnInfo(_spawnList.size(), 2, 5000, new Point3D(-5, 0, 5)));
        _spawnList.add(new SpawnInfo(_spawnList.size(), 3, 5000, new Point3D(6, 2, 6)));
    }

    public NpcInstance getNpc(int id) {
        if (_npcList.containsKey(id)) {
            NpcInstance n = (NpcInstance) _npcList.get(id);
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

    public List<SpawnInfo> getSpawnList() {
        return _spawnList;
    }

}
