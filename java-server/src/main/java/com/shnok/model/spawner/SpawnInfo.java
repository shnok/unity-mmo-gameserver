package com.shnok.model.spawner;

public class SpawnInfo
{
    private int _objectId;
    private int _npcId;
    private int _respawnDelay;
    private boolean _spawned;

    public SpawnInfo(int objectId, int npcId, int respawnDelay) {
        _objectId = objectId;
        _npcId = npcId;
        _respawnDelay = respawnDelay;
        _spawned = false;
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
}
