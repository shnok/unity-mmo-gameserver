package com.shnok.model.entities;

import com.shnok.model.status.PlayerStatus;
import com.shnok.model.status.Status;

public class PlayerInstance extends Entity {
    public String _name;
    private PlayerStatus _status;

    public PlayerInstance(int id, String name) {
        super(id);
        _name = name;
    }

    public String getName() {
        return _name;
    }

    @Override
    public void inflictDamage(int value) {
        _status.setCurrentHp(_status.getCurrentHp() - value);
    }

    @Override
    public PlayerStatus getStatus() {
        return _status;
    }

    @Override
    public void setStatus(Status status) {
        _status = (PlayerStatus) status;
    }

    @Override
    public boolean canMove() {
        return _canMove;
    }

    @Override
    public void moveTo(int x, int y, int z) {

    }

    @Override
    public void onDeath() {

    }
}
