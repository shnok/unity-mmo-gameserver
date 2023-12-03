package com.shnok.javaserver.model.status;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PlayerStatus extends Status {
    private int cp;
    private int maxCp;
    private int mp;
    private int maxMp;
    private int exp;
    private final int maxExp;

    public PlayerStatus() {
        maxHp = 100;
        hp = maxHp;
        level = 1;
        exp = 0;
        maxExp = 100;
        mp = 100;
        maxMp = 100;
        cp = 75;
        maxCp = 75;
    }

    public void addExp(int value) {
        exp += value;
    }
}
