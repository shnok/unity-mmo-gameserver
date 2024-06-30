package com.shnok.javaserver.db.entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "LVLUPGAIN")
public class DBLevelUpGain {
    @Id
    @Column(name = "CLASSID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int classId;

    @Column(name = "DEFAULTHPBASE")
    protected float defaultHpBase;
    @Column(name = "DEFAULTHPADD")
    protected float defaultHpAdd;
    @Column(name = "DEFAULTHPMOD")
    protected float defaultHpMod;

    @Column(name = "DEFAULTMPBASE")
    protected float defaultMpBase;
    @Column(name = "DEFAULTMPADD")
    protected float defaultMpAdd;
    @Column(name = "DEFAULTMPMOD")
    protected float defaultMpMod;

    @Column(name = "DEFAULTCPBASE")
    protected float defaultCpBase;
    @Column(name = "DEFAULTCPADD")
    protected float defaultCpAdd;
    @Column(name = "DEFAULTCPMOD")
    protected float defaultCpMod;

    @Column(name = "CLASS_LVL")
    protected int classLevel;

}
