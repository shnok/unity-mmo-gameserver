package com.shnok.model.entities;

import com.shnok.Server;
import com.shnok.World;
import com.shnok.ai.BaseAI;
import com.shnok.ai.enums.Event;
import com.shnok.model.spawner.SpawnHandler;
import com.shnok.model.spawner.SpawnInfo;
import com.shnok.model.status.NpcStatus;
import com.shnok.model.status.PlayerStatus;
import com.shnok.model.status.Status;
import com.shnok.pathfinding.PathFinding;
import com.shnok.serverpackets.ObjectPosition;
import com.shnok.serverpackets.RemoveObject;

public class NpcInstance extends Entity {
    private int _npcId;
    private NpcStatus _status;
    private boolean _isStatic = false;
    private SpawnInfo _spawnInfo;
    private BaseAI _ai;

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
    public void moveTo(int x, int y, int z) {
        PathFinding.getInstance().findPath((int)getPosX(), (int)getPosY(), (int)getPosZ(), x, y, z);
        System.out.println("AI find path: " + x + "," + y + "," + z);
        if(_ai != null) {
            _ai.notifyEvent(Event.ARRIVED);
        }
    }

    @Override
    public void onDeath() {
        if(_ai != null)
            _ai.notifyEvent(Event.DEAD);

        World.getInstance().removeNPC(this);
        Server.getInstance().broadcastAll(new RemoveObject(getId()));
        SpawnHandler.getInstance().getRegisteredSpawns().get(getId()).setSpawned(false);
        SpawnHandler.getInstance().respawn(getId());
    }

    public void setSpawn(SpawnInfo spawnInfo) {
        _spawnInfo = spawnInfo;
    }

    public SpawnInfo getSpawn() {
        return _spawnInfo;
    }

    public void attachAI(BaseAI ai) {
        _ai = ai;
    }

    public void detachAI() {
        _ai = null;
    }

}
