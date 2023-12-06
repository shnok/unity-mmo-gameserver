package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.SpawnList;

import java.util.List;

public interface SpawnListDao {
    public void addSpawnList(SpawnList spawnList);
    public SpawnList getSpawnListById(int id);
    public List<SpawnList> getAllSpawnList();
}
