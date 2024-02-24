package com.shnok.javaserver.db.entity;

import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.enums.item.WeaponType;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "WEAPON")
public class DBWeapon extends DBItem {
    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @Column(name = "BODYPART")
    @Enumerated(EnumType.STRING)
    private ItemSlot bodyPart;
    @Column(name = "SOULSHOTS")
    private byte soulshots;
    @Column(name = "SPIRITSHOTS")
    private byte spiritshots;
    @Column(name = "P_DAM")
    private int pAtk;
    @Column(name = "M_DAM")
    private int mAtk;
    @Column(name = "RND_DAM")
    private int rndDmg;
    @Column(name = "WEAPONTYPE")
    @Enumerated(EnumType.STRING)
    private WeaponType type;
    @Column(name = "CRITICAL")
    private int critical;
    @Column(name = "HIT_MODIFY")
    private int hitModify;
    @Column(name = "AVOID_MODIFY")
    private int avoidModify;
    @Column(name = "SHIELD_DEF")
    private int shieldDef;
    @Column(name = "SHIELD_DEF_RATE")
    private int shieldDefRate;
    @Column(name = "ATK_SPEED")
    private int atkSpd;
    @Column(name = "MP_CONSUME")
    private int mpConsume;
}
