package com.shnok.javaserver.dto.external.serverpackets.item;

import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class InventoryItemListPacket extends AbstractItemPacket {
    private final boolean showWindow;
    private final List<ItemInstance> items;

    public InventoryItemListPacket(PlayerInstance player, boolean showWindow) {
        super(ServerPacketType.InventoryItemList.getValue());

        this.showWindow = showWindow;
        this.items = new ArrayList<>();

        for (ItemInstance item : player.getInventory().getItems()) {
            if (!item.isQuestItem()) {
                items.add(item);
            }
        }

        writeMe();
        buildPacket();
    }

    public void writeMe() {
        writeB((byte) (showWindow ? 0x01 : 0x00));
        writeI(items.size());
        items.forEach(this::writeItem);
    }
}
