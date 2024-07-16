package com.shnok.javaserver.model.template;

import com.shnok.javaserver.db.entity.DBCharTemplate;
import com.shnok.javaserver.db.entity.DBCharacter;
import com.shnok.javaserver.db.entity.DBLevelUpGain;
import com.shnok.javaserver.db.repository.CharTemplateRepository;
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
        DBCharTemplate template = CharTemplateRepository.getInstance().getTemplateByClassId(character.getClassId().getId());

        this.classId = character.getClassId();
        this.race = template.getRace();
        this.baseSTR = template.getStr();
        this.baseDEX = template.getDex();
        this.baseINT = template.get_int();
        this.baseWIT = template.getWit();
        this.baseMEN = template.getMen();
        this.baseCON = template.getCon();
        this.baseHpReg = 1.5f;
        this.baseMpReg = 0.9f;
        this.baseCpReg = 1f;
        this.basePAtk = template.getPAtk();
        this.baseMAtk = template.getMAtk();
        this.basePDef = template.getPDef();
        this.baseMDef = template.getMDef();
        this.baseCritRate = template.getCritical();
        this.basePAtkSpd = template.getPAtkSpd();
        this.baseMAtkSpd = template.getMAtkSpd();
        this.baseAtkRange = 0.733f;
        this.baseWalkSpd = template.getMoveSpd();
        this.baseRunSpd = template.getMoveSpd();
        this.collisionHeight = character.getColH();
        this.collisionRadius = character.getColR();
        this.levelUpGain = LvlUpGainRepository.getInstance().getLevelUpGainByClassId(this.classId.getId());
    }

    public float getBaseHpMax(int level) {
        level -= getClassId().getBaseLevel();
        float hpmod = levelUpGain.getDefaultHpMod() * level;
        float hpmax = (levelUpGain.getDefaultHpAdd() + hpmod) * level;
        float hpmin = (levelUpGain.getDefaultHpAdd() * level) + hpmod;
        return (hpmax + hpmin) / 2 + levelUpGain.getDefaultHpBase();
    }

    public float getBaseMpMax(int level) {
        level -= getClassId().getBaseLevel();
        float mpmod = levelUpGain.getDefaultMpMod() * level;
        float mpmax = (levelUpGain.getDefaultMpAdd() + mpmod) * level;
        float mpmin = (levelUpGain.getDefaultMpAdd() * level) + mpmod;
        return (mpmax + mpmin) / 2 + levelUpGain.getDefaultMpBase();
    }

    public float getBaseCpMax(int level) {
        level -= getClassId().getBaseLevel();
        float cpmod = levelUpGain.getDefaultCpMod() * level;
        float cpmax = (levelUpGain.getDefaultCpAdd() + cpmod) * level;
        float cpmin = (levelUpGain.getDefaultCpAdd() * level) + cpmod;
        return (cpmax + cpmin) / 2 + levelUpGain.getDefaultCpBase();
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
