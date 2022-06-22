package com.shnok.model.status;

public class PlayerStatus extends Status {
    private final int _stamina;
    private final int _maxStamina;
    private int _exp;
    private final int _maxExp;

    public PlayerStatus() {
        _maxHp = 100;
        _hp = _maxHp;
        _maxStamina = 100;
        _stamina = _maxStamina;
        _level = 1;
        _exp = 0;
        _maxExp = 100;
    }

    public int getStamina() {
        return _stamina;
    }

    public int getMaxStamina() {
        return _maxStamina;
    }

    public void addExp(int value) {
        _exp += value;
    }
}
