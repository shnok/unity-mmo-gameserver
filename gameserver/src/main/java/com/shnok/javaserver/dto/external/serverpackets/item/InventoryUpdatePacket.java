package com.shnok.javaserver.dto.external.serverpackets.item;

import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.item.ItemInfo;
import com.shnok.javaserver.model.object.ItemInstance;
import javolution.util.FastList;

import java.util.List;

public class InventoryUpdatePacket extends AbstractItemPacket {

    private final List<ItemInfo> items;

    public InventoryUpdatePacket(List<ItemInstance> items) {
        super(ServerPacketType.InventoryUpdate.getValue());
        this.items = new FastList<>();

        addItems(items);
    }

    public void addItem(ItemInstance item) {
        if (item != null) {
            items.add(new ItemInfo(item));
        }
    }

    public void addNewItem(ItemInstance item) {
        if (item != null) {
            items.add(new ItemInfo(item, 1));
        }
    }

    public void addModifiedItem(ItemInstance item) {
        if (item != null) {
            items.add(new ItemInfo(item, 2));
        }
    }

    public void addRemovedItem(ItemInstance item) {
        if (item != null) {
            items.add(new ItemInfo(item, 3));
        }
    }

    public void addItems(List<ItemInstance> items) {
        if (items != null) {
            for (ItemInstance item : items) {
                if (item != null) {
                    this.items.add(new ItemInfo(item));
                }
            }
        }
    }

    public void writeMe() {
        writeI(items.size());

        for (ItemInfo item : items) {
            writeI(item.getChange());
            writeItem(item);
        }

        buildPacket();
    }
}
