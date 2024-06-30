package com.shnok.javaserver.db.entity;

import com.shnok.javaserver.enums.NpcType;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "NPC")
public class DBNpc {
    @Id
    private int id;
    private int idTemplate;
    private String name;
    private int serverSideName;
    private String title;
    private int serverSideTitle;
    @Column(name = "class")
    private String npcClass;
    @Column(name = "collision_radius")
    private float collisionRadius;
    @Column(name = "collision_height")
    private float collisionHeight;
    private int level;
    private String sex;
    @Enumerated(EnumType.STRING)
    private NpcType type;
    @Column(name = "attackrange")
    private float attackRange;
    private int hp;
    private int mp;
    @Column(name = "hpreg")
    private float hpReg;
    @Column(name = "mpreg")
    private float mpReg;
    private byte str;
    private byte con;
    private byte dex;
    @Column(name = "int")
    private byte intStat;
    private byte wit;
    private byte men;
    private int exp;
    private int sp;
    private int patk;
    private int pdef;
    private int matk;
    private int mdef;
    private int atkspd;
    private float aggro;
    private int matkspd;
    private int rhand;
    private int lhand;
    private int armor;
    private int walkspd;
    private int runspd;
    @Column(name = "faction_id")
    private String factionId;
    @Column(name = "faction_range")
    private float factionRange;
    private int isUndead;
    @Column(name = "absorb_level")
    private int absorbLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "absorb_type")
    private AbsorbType absorbType;

    public enum AbsorbType {
        FULL_PARTY,
        LAST_HIT,
        PARTY_ONE_RANDOM
    }
}
