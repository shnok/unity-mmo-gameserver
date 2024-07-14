package com.shnok.javaserver.model.template;

import com.shnok.javaserver.db.entity.DBNpc;
import com.shnok.javaserver.enums.NpcType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class NpcTemplate extends EntityTemplate {
    public final int npcId;
    public final int idTemplate;
    public final NpcType type;
    public final String npcClass;
    public final String name;
    public final String title;
    public final byte level;
    public final int rewardExp;
    public final int rewardSp;
    public final float aggroRange;
    public final int rhand;
    public final int lhand;
    public final int armor;
    public final String factionId;
    public final float factionRange;

    public NpcTemplate(DBNpc npc) {
        this.npcId = npc.getIdTemplate();
        this.idTemplate = npc.getIdTemplate();
        this.baseSTR = npc.getStr();
        this.baseDEX = npc.getDex();
        this.baseINT = npc.getIntStat();
        this.baseWIT = npc.getWit();
        this.baseMEN = npc.getMen();
        this.baseHpMax = npc.getHp();
        this.baseMpMax = npc.getMp();
        this.baseHpReg = npc.getHpReg();
        this.baseMpReg = npc.getMpReg();
        this.basePAtk = npc.getPatk();
        this.baseMAtk = npc.getMatk();
        this.basePDef = npc.getPdef();
        this.baseMDef = npc.getMdef();
        this.basePAtkSpd = npc.getAtkspd();
        this.baseMAtkSpd = npc.getMatkspd();
        this.baseAtkRange = npc.getAttackRange();
        this.baseWalkSpd = npc.getWalkspd();
        this.baseRunSpd = npc.getRunspd();
        this.collisionHeight = npc.getCollisionHeight();
        this.collisionRadius = npc.getCollisionRadius();

        this.name = npc.getName();
        this.title = npc.getTitle();
        this.level = (byte) npc.getLevel();
        this.type = npc.getType();
        this.rewardExp = npc.getExp();
        this.rewardSp = npc.getSp();
        this.aggroRange = npc.getAggro();
        this.rhand = npc.getRhand();
        this.lhand = npc.getLhand();
        this.armor = npc.getArmor();
        this.factionId = npc.getFactionId();
        this.factionRange = npc.getFactionRange();
        this.npcClass = npc.getNpcClass();
    }
}
