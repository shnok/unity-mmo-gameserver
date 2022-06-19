package com.shnok.model.spawner;

import com.shnok.model.Point3D;

public class SpawnInfo
{
    private int _objectId;
    private int _npcId;
    private int _respawnDelay;
    private boolean _spawned;
    private Point3D _spawnPos;
    private boolean _randomSpawn;

    public SpawnInfo(int objectId, int npcId, int respawnDelay, Point3D spawnPos) {
        _objectId = objectId;
        _npcId = npcId;
        _respawnDelay = respawnDelay;
        _spawnPos = spawnPos;
        _spawned = false;
        _randomSpawn = false;
    }

    public SpawnInfo(int objectId, int npcId, int respawnDelay) {
        _objectId = objectId;
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
    public int getObjectId() {
        return _objectId;
    }
    public void setObjectId(int _objectId) {
        this._objectId = _objectId;
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
