package com.shnok.javaserver.db.entity;

import com.shnok.javaserver.enums.ClassId;
import com.shnok.javaserver.enums.Race;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "CHARACTER")
@Data
public class DBCharacter {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "account_name")
    private String accountName;

    @Column(name = "char_name")
    private String charName;

    @Column(name = "race")
    private Race race;

    @Column(name = "class_id")
    @Enumerated(EnumType.ORDINAL)
    private ClassId classId;

    @Column(name = "title")
    private String title;

    @Column(name = "access_level")
    private Integer accessLevel;

    @Column(name = "online")
    private boolean online;

    @Column(name = "char_slot")
    private Byte charSlot;

    @Column(name = "last_login")
    private Long lastLogin;

    @Column(name = "delete_time")
    private Long delete_time;

    @Column(name = "level")
    private Integer level;

    @Column(name = "max_hp")
    private Integer maxHp;

    @Column(name = "hp")
    private Integer curHp;

    @Column(name = "max_cp")
    private Integer maxCp;

    @Column(name = "cp")
    private Integer curCp;

    @Column(name = "max_mp")
    private Integer maxMp;

    @Column(name = "mp")
    private Integer curMp;

    @Column(name = "acc")
    private Integer acc;

    @Column(name = "critical")
    private Integer critical;

    @Column(name = "evasion")
    private Integer evasion;

    @Column(name = "m_atk")
    private Integer mAtk;

    @Column(name = "m_def")
    private Integer mDef;

    @Column(name = "m_spd")
    private Integer mSpd;

    @Column(name = "p_atk")
    private Integer pAtk;

    @Column(name = "p_def")
    private Integer pDef;

    @Column(name = "p_spd")
    private Integer pSpd;

    @Column(name = "run_spd")
    private Integer runSpd;

    @Column(name = "walk_spd")
    private Integer walkSpd;

    @Column(name = "str")
    private Byte str;

    @Column(name = "con")
    private Byte con;

    @Column(name = "dex")
    private Byte dex;

    @Column(name = "_int")
    private Byte _int;

    @Column(name = "men")
    private Byte men;

    @Column(name = "wit")
    private Byte wit;

    @Column(name = "face")
    private Byte face;

    @Column(name = "hair_style")
    private Byte hairStyle;

    @Column(name = "hair_color")
    private Byte hairColor;

    @Column(name = "sex")
    private Byte sex;

    @Column(name = "heading")
    private Float heading;

    @Column(name = "x")
    private Float posX;

    @Column(name = "y")
    private Float posY;

    @Column(name = "z")
    private Float posZ;

    @Column(name = "colR")
    private Float colR;

    @Column(name = "colH")
    private Float colH;

    @Column(name = "exp")
    private Integer exp;

    @Column(name = "sp")
    private Integer sp;

    @Column(name = "karma")
    private Integer karma;

    @Column(name = "pvp_kills")
    private Integer pvpKills;

    @Column(name = "pk_kills")
    private Integer pkKills;

    @Column(name = "clan_id")
    private Integer clan_id;

    @Column(name = "max_weight")
    private Integer maxWeight;

    @Column(name = "create_date")
    private Long createDate;
}