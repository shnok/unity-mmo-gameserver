package com.shnok.javaserver.model.item;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.enums.ItemLocation;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import javolution.util.FastList;

import java.util.List;

public abstract class ItemContainer {
    protected final List<ItemInstance> items;

    public ItemContainer() {
        items = new FastList<>();
    }

    protected abstract Entity getOwner();

    protected abstract ItemLocation getBaseLocation();

    // returns the ownerID of the inventory
    public int getOwnerId() {
        return getOwner() == null ? 0 : getOwner().getId();
    }

    // returns the quantity of items in the inventory
    public int getSize() {
        return items.size();
    }

    // returns the list of items in inventory
    public ItemInstance[] getItems() {
        return items.toArray(new ItemInstance[items.size()]);
    }

    // Returns the item from inventory by using its itemId
    public ItemInstance getItemByItemId(int itemId) {
        for (ItemInstance item : items) {
            if ((item != null) && (item.getItemId() == itemId)) {
                return item;
            }
        }

        return null;
    }

    // Returns item from inventory by using its objectId
    public ItemInstance getItemByObjectId(int objectId) {
        for (ItemInstance item : items){
            if (item.getId() == objectId) {
                return item;
            }
        }

        return null;
    }

    // gets count of item in the inventory
    public int getInventoryItemCount(int itemId, int enchantLevel) {
        int count = 0;

        for (ItemInstance item : items) {
            if ((item.getItemId() == itemId)) {
                if (item.isStackable()) {
                    count = item.getCount();
                } else {
                    count++;
                }
            }
        }

        return count;
    }

    // adds item to inventory
    protected void addItem(ItemInstance item) {
        items.add(item);
    }

    // Removes item from inventory
    protected void removeItem(ItemInstance item) {
        items.remove(item);
    }

    // refresh total weight of inventory
    protected void refreshWeight() {
        //TODO calculate weight
    }

    // destroy item from inventory and updates database
    public ItemInstance destroyItem(ItemInstance item, PlayerInstance actor) {
        synchronized (item) {
            // check if item is present in this container
            if (!items.contains(item)) {
                return null;
            }

            removeItem(item);
            //TODO destroy item in db
            //ItemTable.getInstance().destroyItem(process, item, actor);
            //TODO destroy update db
            //item.updateDatabase();
            refreshWeight();
        }

        return item;
    }

    // destroy item from inventory by using its objectID and updates database
    public ItemInstance destroyItem(int objectId, int count, PlayerInstance actor) {
        ItemInstance item = getItemByObjectId(objectId);
        if (item == null) {
            return null;
        }

        // Adjust item quantity
        if (item.getCount() > count) {
            synchronized (item) {
                item.changeCount(-count, actor);
                //TODO Update db
                //item.updateDatabase();
                refreshWeight();
            }
            return item;
        }

        // Directly drop entire item
        return destroyItem(item, actor);
    }

    // destroy item from inventory by using its itemId and updates database

    public ItemInstance destroyItemByItemId(int itemId, int count, PlayerInstance actor) {
        ItemInstance item = getItemByItemId(itemId);
        if (item == null) {
            return null;
        }

        synchronized (item) {
            // Adjust item quantity
            if (item.getCount() > count) {
                item.changeCount(-count, actor);
            } else {
                return destroyItem(item, actor);
            }

            //TODO Update db
            //item.updateDatabase();
            refreshWeight();
        }

        return item;
    }

    public int getMoney() {
        int count = 0;

        for (ItemInstance item : items) {
            if (item.getItemId() == Config.MONEY_ID) {
                count = item.getCount();
                return count;
            }
        }

        return count;
    }
}