package com.shnok.model.entities;

import com.shnok.Server;
import com.shnok.World;
import com.shnok.ai.enums.Event;
import com.shnok.model.spawner.SpawnHandler;
import com.shnok.model.spawner.SpawnInfo;
import com.shnok.model.status.NpcStatus;
import com.shnok.model.status.Status;
import com.shnok.serverpackets.RemoveObject;

public class NpcInstance extends Entity {
    private boolean _isStatic = false;
    private int _npcId;
    private NpcStatus _status;
    private SpawnInfo _spawnInfo;

    public NpcInstance(int id, int npcId) {
        super(id);
    }

    public NpcInstance(SpawnInfo spawnInfo) {
        super(spawnInfo.getObjectId());
        setSpawn(spawnInfo);
        setPosition(spawnInfo.getSpawnPos());
        setNpcId(spawnInfo.getNpcId());
    }

    public int getNpcId() {
        return _npcId;
    }

    public void setNpcId(int npcId) {
        _npcId = npcId;
    }

    public boolean isStatic() {
        return _isStatic;
    }

    public void setStatic(boolean isStatic) {
        _isStatic = isStatic;
    }

    @Override
    public void inflictDamage(int value) {
        _status.setCurrentHp(_status.getCurrentHp() - value);

        if (_status.getCurrentHp() <= 0) {
            onDeath();
        }
    }

    @Override
    public NpcStatus getStatus() {
        return _status;
    }

    @Override
    public void setStatus(Status status) {
        _status = (NpcStatus) status;
    }

    @Override
    public boolean canMove() {
        return _canMove && !_isStatic;
    }

    @Override
    public void onDeath() {
        if (_ai != null)
            _ai.notifyEvent(Event.DEAD);

        World.getInstance().removeNPC(this);
        Server.getInstance().broadcastAll(new RemoveObject(getId()));
        SpawnHandler.getInstance().getRegisteredSpawns().get(getId()).setSpawned(false);
        SpawnHandler.getInstance().respawn(getId());
    }

    public SpawnInfo getSpawn() {
        return _spawnInfo;
    }

    public void setSpawn(SpawnInfo spawnInfo) {
        _spawnInfo = spawnInfo;
    }
}
