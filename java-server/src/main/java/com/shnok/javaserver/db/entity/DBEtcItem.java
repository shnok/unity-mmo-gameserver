package com.shnok.javaserver.db.entity;

import com.shnok.javaserver.enums.item.ConsumeType;
import com.shnok.javaserver.enums.item.EtcItemType;
import com.shnok.javaserver.enums.item.ItemSlot;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ETCITEM")
public class DBEtcItem extends DBItem {
    @Id
    @Column(name = "ITEM_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected int id;
    @Column(name = "CONSUME_TYPE")
    @Enumerated(EnumType.STRING)
    @Access(AccessType.PROPERTY)
    private ConsumeType consumeType;
    @Column(name = "ITEM_TYPE")
    @Enumerated(EnumType.STRING)
    private EtcItemType type;
    @Column(name = "ID_NAME")
    private String idName;
    @Column(name = "DROP_CATEGORY")
    private int dropCategory;
    @Transient
    private ItemSlot bodyPart;
    @Transient
    private boolean stackable;

    public void setConsumeType(ConsumeType type) {
        consumeType = type;
        stackable = type == ConsumeType.stackable;
    }
}
