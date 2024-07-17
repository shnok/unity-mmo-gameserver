package com.shnok.javaserver.model.item;

import com.shnok.javaserver.db.entity.DBItem;
import com.shnok.javaserver.model.object.ItemInstance;
import lombok.Getter;

@Getter
public class ItemInfo {
    private int objectId;
    private DBItem item;
    private int enchant;
    private long count;
    private int price;
    private byte equipped;
    private byte category;
    private int slot;

    /**
     * The action to do clientside (1=ADD, 2=MODIFY, 3=REMOVE)
     */
    private int change;
    private int time;
    private int location;

    public ItemInfo(ItemInstance item) {
        if (item == null) {
            return;
        }

        // Get the Identifier of the L2ItemInstance
        objectId = item.getId();

        // Get the L2Item of the L2ItemInstance
        this.item = item.getItem();

        // Get the enchant level of the L2ItemInstance
        enchant = item.getEnchantLevel();

        // Get the quantity of the L2ItemInstance
        count = item.getCount();

        // Verify if the L2ItemInstance is equipped
        equipped = item.isEquipped() ? (byte) 1 : (byte) 0;

        // Get the action to do clientside
        switch (item.getLastChange()) {
            case (ItemInstance.ADDED):
                change = 1;
                break;
            case (ItemInstance.MODIFIED):
                change = 2;
                break;
            case (ItemInstance.REMOVED):
                change = 3;
                break;
        }

        // Get shadow item mana
        location = item.getLocation().getValue();
        category = item.getItemCategory().getValue();
        time = item.getItem().getDuration();
        slot = item.getSlot();
    }

    public ItemInfo(ItemInstance item, int change) {
        this(item);
        this.change = change;
    }
}