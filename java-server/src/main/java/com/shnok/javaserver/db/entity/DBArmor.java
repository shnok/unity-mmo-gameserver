package com.shnok.javaserver.db.entity;

import com.shnok.javaserver.enums.item.ArmorType;
import com.shnok.javaserver.enums.item.ItemSlot;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ARMOR")
public class DBArmor extends DBItem {
    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "BODYPART")
    @Enumerated(EnumType.STRING)
    private ItemSlot bodyPart;
    @Column(name = "ARMOR_TYPE")
    @Enumerated(EnumType.STRING)
    private ArmorType type;
    @Column(name = "AVOID_MODIFY")
    private int avoidModify;
    @Column(name = "P_DEF")
    private int pDef;
    @Column(name = "M_DEF")
    private int mDef;
    @Column(name = "MP_BONUS")
    private int mpBonus;
    @Transient
    private boolean stackable = false;
}
