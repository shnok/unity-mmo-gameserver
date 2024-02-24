package com.shnok.javaserver.model.template;

import com.shnok.javaserver.db.entity.DBCharTemplate;
import com.shnok.javaserver.db.entity.DBCharacter;

public class PlayerTemplate extends EntityTemplate {
    public PlayerTemplate(DBCharacter character) {
        this.baseSTR = character.getStr();
        this.baseDEX = character.getDex();
        this.baseINT = character.get_int();
        this.baseWIT = character.getWit();
        this.baseMEN = character.getMen();
        this.baseHpMax = character.getMaxHp();
        this.baseMpMax = character.getMaxMp();
        this.baseCpMax = character.getMaxCp();
        this.baseHpReg = 1.5f;
        this.baseMpReg = 0.9f;
        this.basePAtk = character.getPAtk();
        this.baseMAtk = character.getMAtk();
        this.basePDef = character.getPDef();
        this.baseMDef = character.getMDef();
        this.basePAtkSpd = character.getPSpd();
        this.baseMAtkSpd = character.getMSpd();
        this.baseAtkRange = 0.733f;
        this.baseWalkSpd = character.getWalkSpd();
        this.baseRunSpd = character.getRunSpd();
        this.collisionHeight = character.getColH();
        this.collisionRadius = character.getColR();
    }
}
