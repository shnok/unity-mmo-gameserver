package com.shnok.model.entities;

import com.shnok.model.status.NpcStatus;
import com.shnok.model.status.PlayerStatus;
import com.shnok.model.status.Status;

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
    void inflictDamage(int value) {
        _status.setCurrentHp(_status.getCurrentHp() - value);
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
