package com.shnok.javaserver.model.object;

import com.shnok.javaserver.db.entity.DBEtcItem;
import com.shnok.javaserver.db.entity.DBItem;
import com.shnok.javaserver.enums.ItemLocation;
import com.shnok.javaserver.enums.item.ConsumeType;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Getter
@Setter
@Log4j2
public class ItemInstance extends GameObject {
    private int ownerId;
    // id of the item in db
    private int itemId;
    private DBItem item;
    // quantity
    private int count;
    // location of the item : inventory, equipment, warehouse
    private ItemLocation location;
    // slot where item is stored
    private int slot;
    private boolean stackable;

    public ItemInstance(int ownerId, DBItem item) {
        this.itemId = item.getId();
        this.ownerId = ownerId; //TODO check if better to use charId instead of entity id
        this.item = item;
        this.stackable = false;

        if(item instanceof DBEtcItem) {
            ConsumeType consumeType = ((DBEtcItem) item).getConsumeType();
            stackable = consumeType == ConsumeType.stackable || consumeType == ConsumeType.asset;
        }
    }

    // Sets the location of the item
    public void setLocation(ItemLocation loc) {
        setLocation(loc, 0);
    }

    // sets the location of the item
    public void setLocation(ItemLocation location, int slot) {
        if ((this.location == location) && (this.slot == slot)) {
            return;
        }
        this.location = location;
        this.slot = slot;
    }

    public void changeCount(int value, PlayerInstance creator) {
        if (value == 0) {
            return;
        }
        if ((value > 0) && (count > (Integer.MAX_VALUE - value))) {
            count = Integer.MAX_VALUE;
        } else {
            count += value;
        }
        if (count < 0) {
            count = 0;
        }
    }

    public boolean isEquipped() {
        return (location == ItemLocation.EQUIPPED);
    }

    public void setOwnerId(int newOwner, PlayerInstance creator) {
        log.debug("[{}] New item owner: {}. Actor: {}", getId(), newOwner, creator);
        setOwnerId(newOwner);
    }

    // sets the ownerID of the item
    public void setOwnerId(int newOwner) {
        if (newOwner == ownerId) {
            return;
        }

        log.debug("[{}] New item owner: {}", getId(), newOwner);
        ownerId = newOwner;
    }
}
