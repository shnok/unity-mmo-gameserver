package com.shnok.javaserver.model.template;

import com.shnok.javaserver.enums.MoveType;
import lombok.Data;

@Data
public class EntityTemplate {
    public int baseSTR;
    public int baseCON;
    public int baseDEX;
    public int baseINT;
    public int baseWIT;
    public int baseMEN;
    public int baseHpMax;
    public int baseCpMax;
    public int baseMpMax;
    public float baseHpReg;
    public float baseMpReg;
    public float baseCpReg;
    public int basePAtk;
    public int baseMAtk;
    public int basePDef;
    public int baseMDef;
    public int basePAtkSpd;
    public int baseMAtkSpd;
    public float baseMReuseRate;
    public float baseAtkRange;
    public int baseCritRate;
    public int baseWalkSpd;
    public int baseRunSpd;
    public float collisionRadius;
    public float collisionHeight;

    public EntityTemplate() {}

    public int getBaseMoveSpeed(MoveType moveType) {
        if(moveType == MoveType.RUN) {
            return baseRunSpd;
        }

        return baseWalkSpd;
    }
}
