package com.shnok.javaserver.model.status;

public abstract class Status {
    protected int maxHp;
    protected int hp;
    protected int level;
    protected float moveSpeed = 5f;

    public Status() {
        /*maxHp = 100;
        hp = maxHp;
        maxStamina = 100;
        stamina = maxStamina;
        level = 1;
        exp = 0;
        maxExp = 100;*/
    }

    protected Status(int level, int maxHp) {
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.level = level;
    }

    public int getCurrentHp() {
        return hp;
    }

    public void setCurrentHp(int value) {
        hp = value;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int value) {
        level = value;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }
}
