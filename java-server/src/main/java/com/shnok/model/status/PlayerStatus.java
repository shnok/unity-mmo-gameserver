package com.shnok.model.status;

public class PlayerStatus extends Status {
    private int _stamina;
    private int _maxStamina;
    private int _exp;
    private int _maxExp;

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
