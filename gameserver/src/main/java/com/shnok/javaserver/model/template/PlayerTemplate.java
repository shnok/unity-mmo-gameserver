package com.shnok.javaserver.model.template;

import com.shnok.javaserver.db.entity.DBCharacter;
import com.shnok.javaserver.db.entity.DBLevelUpGain;
import com.shnok.javaserver.db.repository.LvlUpGainRepository;
import com.shnok.javaserver.enums.ClassId;
import com.shnok.javaserver.enums.Race;
import lombok.Getter;

@Getter
public class PlayerTemplate extends EntityTemplate {
    private final Race race;
    private final ClassId classId;
    private final DBLevelUpGain levelUpGain;

    public PlayerTemplate(DBCharacter character) {
        this.race = character.getRace();
        this.classId = character.getClassId();
        this.baseSTR = character.getStr();
        this.baseDEX = character.getDex();
        this.baseINT = character.get_int();
        this.baseWIT = character.getWit();
        this.baseMEN = character.getMen();
        this.baseHpReg = 1.5f;
        this.baseMpReg = 0.9f;
        this.baseCpReg = 1f;
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
        this.levelUpGain = LvlUpGainRepository.getInstance().getLevelUpGainByClassId(this.classId.getId());
    }

    public float getBaseHpMax(int level) {
        //TODO: remove base class level to level
        level -= 1;
        float hpmod = levelUpGain.getDefaultHpMod() * level;
        float hpmax = (levelUpGain.getDefaultHpAdd() + hpmod) * level;
        float hpmin = (levelUpGain.getDefaultHpAdd() * level) + hpmod;
        return (hpmax + hpmin) / 2;
    }

    public float getBaseMpMax(int level) {
        //TODO: remove base class level to level
        level -= 1;
        float mpmod = levelUpGain.getDefaultMpMod() * level;
        float mpmax = (levelUpGain.getDefaultMpAdd() + mpmod) * level;
        float mpmin = (levelUpGain.getDefaultMpAdd() * level) + mpmod;
        return (mpmax + mpmin) / 2;
    }

    public float getBaseCpMax(int level) {
        //TODO: remove base class level to level
        level -= 1;
        float cpmod = levelUpGain.getDefaultCpMod() * level;
        float cpmax = (levelUpGain.getDefaultCpAdd() + cpmod) * level;
        float cpmin = (levelUpGain.getDefaultCpAdd() * level) + cpmod;
        return (cpmax + cpmin) / 2;
    }

    public float getBaseHpReg(int level) {
        return (level > 10) ? ((level - 1) / 10.0f) : 0.5f;
    }

    public float getBaseMpReg(int level) {
        return 0.3f * ((level - 1) / 10.0f);
    }

    public float getBaseCpReg(int level) {
        return (level > 10) ? ((level - 1) / 10.0f) : 0.5f;
    }
}
