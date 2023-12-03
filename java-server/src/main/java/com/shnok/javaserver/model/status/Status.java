package com.shnok.javaserver.model.status;

import lombok.Data;

@Data
public abstract class Status {
    protected int maxHp;
    protected int hp;
    protected int level;
    protected float moveSpeed = 5f;

    public Status() {
    }

    protected Status(int level, int maxHp) {
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.level = level;
    }
}
