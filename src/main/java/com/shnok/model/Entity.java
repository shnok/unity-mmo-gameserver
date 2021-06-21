package com.shnok.model;

/**
 * This class represents all entities in the world.<BR>
 * <BR>
 * Such as : Players or AIs<BR>
 * <BR>
 */
public class Entity extends GameObject {
    private EntityStatus _status;

    public Entity(int id) {
        super(id);
        _status = new EntityStatus();
    }

    public Entity(int id, String name) {
        super(id, name);
        _status = new EntityStatus();
    }

    public void setCurrentHp(int value) {
        _status.setCurrentHp(value);
    }

    public void inflictDamage(int value) {
        _status.setCurrentHp(_status.getCurrentHp() - value);
    }

    public void setLevel(int value) {
        _status.setLevel(value);
    }

    public void addLevel(int value) {
        _status.setLevel(_status.getLevel() + value);
    }

    public void addExp(int value) {
        _status.addExp(value);
    }
}
