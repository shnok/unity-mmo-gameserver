package com.shnok.javaserver.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "CHARACTER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Character {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "char_name")
    private String charName;

    @Column(name = "race")
    private byte race;

    @Column(name = "class_id")
    private byte classId;

    @Column(name = "title")
    private String title;

    @Column(name = "access_level")
    private Integer accessLevel;

    @Column(name = "online")
    private boolean online;

    @Column(name = "online_time")
    private Long onlineTime;

    @Column(name = "char_slot")
    private byte charSlot;

    @Column(name = "last_login")
    private Long lastLogin;

    @Column(name = "delete_time")
    private Long delete_time;

    @Column(name = "level")
    private int level;

    @Column(name = "max_hp")
    private int maxHp;

    @Column(name = "hp")
    private int curHp;

    @Column(name = "max_cp")
    private int maxCp;

    @Column(name = "cp")
    private int curCp;

    @Column(name = "max_mp")
    private int maxMp;

    @Column(name = "cur_mp")
    private int curMp;

    @Column(name = "acc")
    private int acc;

    @Column(name = "crit")
    private int crit;

    @Column(name = "evasion")
    private int evasion;

    @Column(name = "m_atk")
    private int mAtk;

    @Column(name = "m_def")
    private int mDef;

    @Column(name = "m_spd")
    private int mSpd;

    @Column(name = "p_atk")
    private int pAtk;

    @Column(name = "p_def")
    private int pDef;

    @Column(name = "p_spd")
    private int pSpd;

    @Column(name = "run_spd")
    private int runSpd;

    @Column(name = "walk_spd")
    private int walkSpd;

    @Column(name = "str")
    private byte str;

    @Column(name = "con")
    private byte con;

    @Column(name = "dex")
    private byte dex;

    @Column(name = "_int")
    private byte _int;

    @Column(name = "men")
    private byte men;

    @Column(name = "wit")
    private byte wit;

    @Column(name = "face")
    private byte face;

    @Column(name = "hair_style")
    private byte hairStyle;

    @Column(name = "hair_color")
    private byte hairColor;

    @Column(name = "sex")
    private byte sex;

    @Column(name = "heading")
    private float heading;

    @Column(name = "x")
    private float posX;

    @Column(name = "y")
    private float posY;

    @Column(name = "z")
    private float posZ;

    @Column(name = "colR")
    private float colR;

    @Column(name = "colH")
    private float colH;

    @Column(name = "exp")
    private long exp;

    @Column(name = "sp")
    private long sp;

    @Column(name = "karma")
    private int karma;

    @Column(name = "pvp_kills")
    private int pvpKills;

    @Column(name = "pk_kills")
    private int pkKills;

    @Column(name = "clan_id")
    private int clan_id;

    @Column(name = "max_weight")
    private int maxWeight;
}