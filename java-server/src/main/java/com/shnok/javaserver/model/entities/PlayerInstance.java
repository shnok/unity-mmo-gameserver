package com.shnok.javaserver.model.entities;

import com.shnok.javaserver.model.status.PlayerStatus;
import com.shnok.javaserver.model.status.Status;

public class PlayerInstance extends Entity {
    public String name;
    private PlayerStatus status;

    public PlayerInstance(int id, String name) {
        super(id);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void inflictDamage(int value) {
        status.setHp(status.getHp() - value);
    }

    @Override
    public PlayerStatus getStatus() {
        return status;
    }

    @Override
    public void setStatus(Status status) {
        this.status = (PlayerStatus) status;
    }

    @Override
    public boolean canMove() {
        return canMove;
    }

    @Override
    public boolean moveTo(int x, int y, int z) {

        return false;
    }

    @Override
    public void onDeath() {

    }
}
