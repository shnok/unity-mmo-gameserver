package com.shnok.javaserver.db.entity;

import com.shnok.javaserver.enums.ItemLocation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PLAYER_ITEM")
public class DBPlayerItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "object_id")
    private int objectId;

    @Column(name = "owner_id")
    private int ownerId;

    @Column(name = "item_id")
    private int itemId;

    @Column(name = "loc")
    private ItemLocation location;

    @Column(name = "slot")
    private int slot;

    @Column(name = "count")
    private int count;

    @Column(name = "enchant_level")
    private byte enchantLevel;

    @Column(name = "price_sell")
    private int priceSell;

    @Column(name = "price_buy")
    private int priceBuy;
}