package com.shnok.model;

public class EntityStatus {
    private int _maxHp;
    private int _hp;
    private int _stamina;
    private int _maxStamina;
    private int _level;
    private int _exp;
    private int _maxExp;

    public EntityStatus() {
        _maxHp = 100;
        _hp = _maxHp;
        _maxStamina = 100;
        _stamina = _maxStamina;
        _level = 1;
        _exp = 0;
        _maxExp = 100;
    }

    public void setCurrentHp(int value) {
        _hp = value;
    }

    public int getCurrentHp() {
        return _hp;
    }

    public void setLevel(int value) {
        _level += value;
    }

    public int getLevel() {
        return _level;
    }

    public void addExp(int value) {
        _exp += value;
    }
}
