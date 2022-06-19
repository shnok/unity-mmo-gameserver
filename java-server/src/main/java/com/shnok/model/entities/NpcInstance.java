package com.shnok.model.entities;

import com.shnok.Server;
import com.shnok.World;
import com.shnok.model.spawner.SpawnHandler;
import com.shnok.model.spawner.SpawnInfo;
import com.shnok.model.status.NpcStatus;
import com.shnok.model.status.PlayerStatus;
import com.shnok.model.status.Status;
import com.shnok.serverpackets.ObjectPosition;
import com.shnok.serverpackets.RemoveObject;

public class NpcInstance extends Entity {
    private int _npcId;
    private NpcStatus _status;

    public NpcInstance(int id, int npcId) {
        super(id);
    }

    public int getNpcId() {
        return _npcId;
    }

    public void setNpcId(int npcId) {
        _npcId = npcId;
    }

    @Override
    public void inflictDamage(int value) {
        _status.setCurrentHp(_status.getCurrentHp() - value);

        if(_status.getCurrentHp() <= 0) {
            World.getInstance().removeNPC(this);
            Server.getInstance().broadcastAll(new RemoveObject(getId()));
            SpawnHandler.getInstance().getRegisteredSpawns().get(getId()).setSpawned(false);
            SpawnHandler.getInstance().respawn(getId());
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
}
