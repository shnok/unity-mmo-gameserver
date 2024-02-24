package com.shnok.javaserver.model.item;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.enums.ItemLocation;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.PlayerInstance;

public class PlayerInventory extends Inventory {
    private final PlayerInstance owner;
    private ItemInstance adena;

    public PlayerInventory(PlayerInstance owner) {
        super();
        this.owner = owner;
    }

    public ItemInstance getMoneyInstance() {
        return adena;
    }

    @Override
    public int getMoney() {
        return adena != null ? adena.getCount() : 0;
    }

    @Override
    protected ItemLocation getEquipLocation() {
        return ItemLocation.EQUIPPED;
    }

    @Override
    protected ItemLocation getBaseLocation() {
        return ItemLocation.INVENTORY;
    }

    @Override
    public ItemInstance destroyItem(ItemInstance item, PlayerInstance actor) {
        item = super.destroyItem(item, actor);

        if ((adena != null) && (adena.getCount() <= 0)) {
            adena = null;
        }

        return item;
    }

    // destroys item from inventory and checks money
    @Override
    public ItemInstance destroyItem(int objectId, int count, PlayerInstance actor) {
        ItemInstance item = super.destroyItem(objectId, count, actor);

        if ((adena != null) && (adena.getCount() <= 0)) {
            adena = null;
        }

        return item;
    }

    // destroy item from inventory by using its itemId and checks money
    @Override
    public ItemInstance destroyItemByItemId(int itemId, int count, PlayerInstance actor) {
        ItemInstance item = super.destroyItemByItemId(itemId, count, actor);

        if ((adena != null) && (adena.getCount() <= 0)) {
            adena = null;
        }

        return item;
    }

    // drop item from inventory and checks money
    @Override
    public ItemInstance dropItem(ItemInstance item, PlayerInstance actor) {
        item = super.dropItem(item, actor);

        if ((adena != null) && ((adena.getCount() <= 0) || (adena.getOwnerId() != getOwnerId()))) {
            adena = null;
        }

        return item;
    }

    // drop item from inventory by using its objectID and money

    @Override
    public ItemInstance dropItem(int objectId, int count, PlayerInstance actor) {
        ItemInstance item = super.dropItem(objectId, count, actor);

        if ((adena != null) && ((adena.getCount() <= 0) || (adena.getOwnerId() != getOwnerId()))) {
            adena = null;
        }

        return item;
    }

    // when removes item from inventory, remove also owner shortcuts.
    @Override
    protected void removeItem(ItemInstance item) {
        // Removes any reference to the item from Shortcut bar
        //getOwner().removeItemFromShortCut(item.getObjectId());

        if (item.getItemId() == Config.MONEY_ID) {
            adena = null;
        }

        super.removeItem(item);
    }
}
