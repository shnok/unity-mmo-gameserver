package com.shnok.javaserver.db.entity;

import com.shnok.javaserver.enums.item.ConsumeType;
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
    private ConsumeType consumeType;
    @Column(name = "ID_NAME")
    private String idName;
    @Column(name = "DROP_CATEGORY")
    private int dropCategory;
}
