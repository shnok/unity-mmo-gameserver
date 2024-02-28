package com.shnok.javaserver.db.interfaces;

import com.shnok.javaserver.db.entity.DBSpawnList;

import java.util.List;

public interface SpawnListDao {
    public void addSpawnList(DBSpawnList spawnList);
    public DBSpawnList getSpawnListById(int id);
    public List<DBSpawnList> getAllSpawnList();

    List<DBSpawnList> getAllMonsters();

    List<DBSpawnList> getAllNPCs();
}
