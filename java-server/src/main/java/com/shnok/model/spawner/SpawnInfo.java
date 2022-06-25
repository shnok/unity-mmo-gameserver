package com.shnok.model.spawner;

import com.shnok.model.Point3D;

public class SpawnInfo {
    private final boolean _randomSpawn;
    private int _id;
    private int _npcId;
    private int _respawnDelay;
    private boolean _spawned;
    private Point3D _spawnPos;

    public SpawnInfo(int id, int npcId, int respawnDelay, Point3D spawnPos) {
        _id = id;
        _npcId = npcId;
        _respawnDelay = respawnDelay;
        _spawnPos = spawnPos;
        _spawned = false;
        _randomSpawn = false;
    }

    public SpawnInfo(int id, int npcId, int respawnDelay) {
        _id = id;
        _npcId = npcId;
        _respawnDelay = respawnDelay;
        _spawned = false;
        _randomSpawn = true;
    }

    public int getNpcId() {
        return _npcId;
    }

    public void setNpcId(int _npcId) {
        this._npcId = _npcId;
    }

    public int getRespawnDelay() {
        return _respawnDelay;
    }

    public void setRespawnDelay(int _respawnDelay) {
        this._respawnDelay = _respawnDelay;
    }

    public boolean isSpawned() {
        return _spawned;
    }

    public void setSpawned(boolean value) {
        _spawned = value;
    }

    public int getId() {
        return _id;
    }

    public void setId(int _objectId) {
        this._id = _objectId;
    }

    public Point3D getSpawnPos() {
        return _spawnPos;
    }

    public void setSpawnPos(Point3D spawnPos) {
        _spawnPos = spawnPos;
    }

    public boolean isRandomSpawn() {
        return _randomSpawn;
    }
}
