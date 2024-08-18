package com.shnok.javaserver.db.entity;

import com.shnok.javaserver.enums.item.Grade;
import com.shnok.javaserver.enums.item.ItemCategory;
import com.shnok.javaserver.enums.item.ItemSlot;
import com.shnok.javaserver.enums.item.Material;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@ToString
@MappedSuperclass
public abstract class DBItem {
    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @Column(name = "NAME")
    protected String name;
    @Column(name = "WEIGHT")
    protected int weight;
    @Column(name = "MATERIAL")
    @Enumerated(EnumType.STRING)
    protected Material material;
    @Transient
    protected Enum<?> type;
    @Transient
    protected ItemSlot bodyPart;
    @Column(name = "CRYSTAL_TYPE")
    @Enumerated(EnumType.STRING)
    protected Grade grade;
    @Column(name = "DURATION")
    protected int duration;
    @Column(name = "PRICE")
    protected int price;
    @Column(name = "CRYSTAL_COUNT")
    protected int crystalCount;
    @Column(name = "SELLABLE")
    protected boolean sellable;
    @Column(name = "DROPABLE")
    protected boolean droppable;
    @Column(name = "DESTROYABLE")
    protected boolean destroyable;
    @Column(name = "TRADEABLE")
    protected boolean tradeable;
    @Transient
    protected boolean stackable;
}
