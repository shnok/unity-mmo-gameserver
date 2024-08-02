package com.shnok.javaserver.dto.external.serverpackets.item;

import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.item.ItemInfo;

import java.util.List;

public class InventoryUpdatePacket extends AbstractItemPacket {

    public InventoryUpdatePacket(List<ItemInfo> items) {
        super(ServerPacketType.InventoryUpdate.getValue());

        items.forEach(this::writeItem);
        buildPacket();
    }

    public InventoryUpdatePacket(ItemInfo item) {
        super(ServerPacketType.InventoryUpdate.getValue());
        writeItem(item);
        buildPacket();
    }
}
