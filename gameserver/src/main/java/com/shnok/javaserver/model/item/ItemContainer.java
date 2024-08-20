package com.shnok.javaserver.model.item;

import com.shnok.javaserver.db.entity.DBItem;
import com.shnok.javaserver.enums.ItemLocation;
import com.shnok.javaserver.model.object.GameObject;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.service.WorldManagerService;
import com.shnok.javaserver.service.db.ItemTable;
import javolution.util.FastList;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
public abstract class ItemContainer {
    protected final List<ItemInstance> items;
    protected final Entity owner;

    public ItemContainer(Entity owner) {
        this.owner = owner;
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

    // adds item to inventory
    public ItemInstance addItem(ItemInstance item, PlayerInstance actor) {
        ItemInstance olditem = getItemByItemId(item.getItemId());

        // If stackable item is found in inventory just add to current quantity
        if ((olditem != null) && olditem.isStackable()) {
            int count = item.getCount();
            olditem.changeCount(count, actor);

            //TODO update db
//            ItemTable.getInstance().destroyItem(process, item, actor, reference);
//            item.updateDatabase();
//            item = olditem;
//
//            // Updates database
//            if ((item.getItemId() == Config.MONEY_ID) && (count < (10000 * Config.RATE_DROP_ADENA))) {
//                // Small money changes won't be saved to database all the time
//                if ((GameTimeController.getGameTicks() % 5) == 0) {
//                    item.updateDatabase();
//                }
//            } else {
//                item.updateDatabase();
//            }
//

        } else {
            // If item hasn't been found in inventory, create new one
            item.setOwnerId(getOwnerId(), actor);
            item.setLocation(getBaseLocation());

            // Add item in inventory
            addItem(item);

            // Updates database
            //TODO update db
            //item.updateDatabase();
        }

        refreshWeight();
        return item;
    }

    // Adds item to inventory
    public ItemInstance addItem(int itemId, int count, PlayerInstance actor) {
        ItemInstance item = getItemByItemId(itemId);

        // If stackable item is found in inventory just add to current quantity
        if ((item != null) && item.isStackable()) {
            item.changeCount(count, actor);
            // Updates database
            //TODO update db
//            if ((itemId == Config.MONEY_ID) && (count < (10000 * Config.RATE_DROP_ADENA))) {
//                // Small adena changes won't be saved to database all the time
//                if ((GameTimeController.getGameTicks() % 5) == 0) {
//                    item.updateDatabase();
//                }
//            } else {
//                item.updateDatabase();
//            }
        // If item hasn't be found in inventory, create new one
        } else {
            for (int i = 0; i < count; i++) {
                DBItem template = ItemTable.getInstance().getItemById(itemId);
                if (template == null) {
                    log.warn("Item {} not found in DB", itemId);
                    return null;
                }

                //TODO create item in db (new object on the ground)
//                item = ItemTableService.getInstance().createItem(process, itemId, template.isStackable() ? count : 1, actor, reference);
//                item.setOwnerId(getOwnerId());
//                item.setLocation(getBaseLocation());
//
//                // Add item in inventory
//                addItem(item);
//                // Updates database
//                item.updateDatabase();

                // If stackable, end loop as entire count is included in 1 instance of item
                if (template.isStackable()) {
                    break;
                }
            }
        }

        refreshWeight();
        return item;
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

        item.setCount(0);

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
            if (item.getItemId() == server.itemMoneyId()) {
                count = item.getCount();
                return count;
            }
        }

        return count;
    }

    public synchronized int findNextAvailableSlot(int containerSize) {
        boolean[] slots = new boolean[containerSize];

        // Mark occupied slots
        for (ItemInstance item : items) {
            if(item.isEquipped()) {
                continue;
            }

            slots[item.getSlot()] = true;
        }

        // Find the first available slot
        for (int i = 0; i < containerSize; i++) {
            if (!slots[i]) {
                return i;
            }
        }

        return -1; // No available slot found
    }

    /**
     * Delete item object from world
     */
    public void destroy() {
        try {
            updateDatabase();
        } catch (Throwable t) {
            log.error("deletedMe()", t);
        }

        List<GameObject> copy = new FastList<>(items);
        items.clear();

        WorldManagerService.getInstance().removeObjects(copy);
    }

    public void updateDatabase() {
        //TODO: DB update functions
    }
}