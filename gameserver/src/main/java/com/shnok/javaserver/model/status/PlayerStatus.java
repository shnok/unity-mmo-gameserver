package com.shnok.javaserver.model.status;

import com.shnok.javaserver.model.object.entity.PlayerInstance;
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

    public PlayerStatus(PlayerInstance owner, PlayerTemplate playerTemplate) {
        super(owner, playerTemplate.getBaseHpMax());
        this.maxHp = playerTemplate.baseHpMax;
        this.hp = playerTemplate.baseHpMax;
        this.mp = playerTemplate.baseMpMax;
        this.maxMp = playerTemplate.baseMpMax;
        this.cp = playerTemplate.baseCpMax;
        this.maxCp = playerTemplate.baseCpMax;
        this.moveSpeed = playerTemplate.baseRunSpd;
    }
}
