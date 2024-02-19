package com.shnok.javaserver.model.template;

import com.shnok.javaserver.db.entity.CharTemplate;

public class PlayerTemplate extends EntityTemplate {
    public PlayerTemplate(CharTemplate template) {
        this.baseSTR = template.getStr();
        this.baseDEX = template.getDex();
        this.baseINT = template.get_int();
        this.baseWIT = template.getWit();
        this.baseMEN = template.getMen();
        //TODO: get from levelup table
        this.baseHpMax = 80;
        this.baseMpMax = 32;
        this.baseCpMax = 30;
        this.baseHpReg = 1.5f;
        this.baseMpReg = 0.9f;
        this.basePAtk = template.getPAtk();
        this.baseMAtk = template.getMAtk();
        this.basePDef = template.getPDef();
        this.baseMDef = template.getMDef();
        this.basePAtkSpd = template.getPAtkSpd();
        this.baseMAtkSpd = template.getMAtkSpd();
        this.baseAtkRange = 0.733f;
        this.baseWalkSpd = 0;
        this.baseRunSpd = template.getMoveSpd();
        this.collisionHeight = template.getCollisionHeightFemale();
        this.collisionRadius = template.getCollisionRadiusFemale();
    }
}
