package com.shnok.javaserver.dto.external.serverpackets.item;

import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.item.ItemInfo;
import com.shnok.javaserver.model.object.ItemInstance;

import java.util.List;

public class InventoryUpdatePacket extends AbstractItemPacket {

    public InventoryUpdatePacket(ItemInfo item) {
        super(ServerPacketType.InventoryUpdate.getValue());
        writeI(1);
        writeItem(item);
        buildPacket();
    }

    public InventoryUpdatePacket(List<ItemInstance> items) {
        super(ServerPacketType.InventoryUpdate.getValue());
        writeI(items.size());
        items.forEach(this::writeItem);
        buildPacket();
    }
}
