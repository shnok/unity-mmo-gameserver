package com.shnok.javaserver.model.object;

import com.shnok.javaserver.db.entity.DBPlayerItem;
import com.shnok.javaserver.enums.ItemLocation;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
public class ItemInstance extends GameObject {
    // reference to the item db entity

    // ID of the owner
    private int ownerId;
    // quantity
    private int count;
    // can the quantity decrease
    private boolean _decrease = false;
    // location of the item : inventory, equipment, warehouse */
    private ItemLocation loc;
    // slot where item is stored
    private int slot;
    // Level of enchantment of the item
    private byte enchantLevel;
    // price of the item for selling
    private int priceSell;
    // Price of the item for buying
    private int priceBuy;

    // Sets the location of the item
    public void setLocation(ItemLocation loc) {
        setLocation(loc, 0);
    }

    // sets the location of the item
    public void setLocation(ItemLocation loc, int slot) {
        if ((this.loc == loc) && (slot == this.slot)) {
            return;
        }
        this.loc = loc;
        this.slot = slot;
    }
}
