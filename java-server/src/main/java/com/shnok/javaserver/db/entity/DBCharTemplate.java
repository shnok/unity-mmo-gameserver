package com.shnok.javaserver.db.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "CHAR_TEMPLATE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CharTemplate {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int classId;
    @Column
    private String className;
    @Column
    private int raceId;
    @Column(name = "STR")
    private int str;
    @Column(name = "CON")
    private int con;
    @Column(name = "DEX")
    private int dex;
    @Column(name = "_INT")
    private int _int;
    @Column(name = "WIT")
    private int wit;
    @Column(name = "MEN")
    private int men;
    @Column(name = "P_ATK")
    private int pAtk;
    @Column(name = "P_DEF")
    private int pDef;
    @Column(name = "M_ATK")
    private int mAtk;
    @Column(name = "M_DEF")
    private int mDef;
    @Column(name = "P_SPD")
    private int pAtkSpd;
    @Column(name = "M_SPD")
    private int mAtkSpd;
    @Column(name = "ACC")
    private int acc;
    @Column(name = "CRITICAL")
    private int critical;
    @Column(name = "EVASION")
    private int evasion;
    @Column(name = "MOVE_SPD")
    private int moveSpd;
    @Column(name = "X")
    private float posX;
    @Column(name = "Y")
    private int posY;
    @Column(name = "Z")
    private int posZ;
    @Column(name = "F_COL_H")
    private float collisionHeightFemale;
    @Column(name = "F_COL_R")
    private float collisionRadiusFemale;
    @Column(name = "M_COL_H")
    private float collisionHeightMale;
    @Column(name = "M_COL_R")
    private float collisionRadiusMale;

    private int items1;
    private int items2;
    private int items3;
    private int items4;
    private int items5;
}
