package com.shnok.javaserver.model.status;

import com.shnok.javaserver.model.template.PlayerTemplate;
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

    public PlayerStatus(PlayerTemplate playerTemplate) {
        this.maxHp = playerTemplate.baseHpMax;
        this.hp = playerTemplate.baseHpMax;
        level = 1;
        exp = 0;
        maxExp = 100;
        this.mp = playerTemplate.baseMpMax;
        this.maxMp = playerTemplate.baseMpMax;
        this.cp = playerTemplate.baseCpMax;
        this.maxCp = playerTemplate.baseCpMax;
        this.moveSpeed = playerTemplate.baseRunSpd;
    }

    public void addExp(int value) {
        exp += value;
    }
}
