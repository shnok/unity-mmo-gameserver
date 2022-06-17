package com.shnok.model.entities;

import com.shnok.model.Entity;

public class NpcInstance extends Entity {
    private int _npcId;
    public NpcInstance(int id, int npcId) {
        super(id);
    }

    public int getNpcId() {
        return _npcId;
    }

    public void setNpcId(int npcId) {
        _npcId = npcId;
    }
}
