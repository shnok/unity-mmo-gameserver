package com.shnok.javaserver.model.status;

public class PlayerStatus extends Status {
    private final int stamina;
    private final int maxStamina;
    private int exp;
    private final int maxExp;

    public PlayerStatus() {
        maxHp = 100;
        hp = maxHp;
        maxStamina = 100;
        stamina = maxStamina;
        level = 1;
        exp = 0;
        maxExp = 100;
    }

    public int getStamina() {
        return stamina;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public void addExp(int value) {
        exp += value;
    }
}
