package com.shnok.javaserver.dto.external.clientpackets.item;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.item.InventoryUpdatePacket;
import com.shnok.javaserver.model.object.ItemInstance;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.thread.GameClientThread;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RequestInventoryUpdateOrderPacket extends ClientPacket {

    /** client limit */
    private static final int LIMIT = 125;

    @Getter
    public static class InventoryOrder {
        int order;
        int objectID;

        public InventoryOrder(int id, int ord) {
            objectID = id;
            order = ord;
        }
    }

    private final List<InventoryOrder> orderList;

    public RequestInventoryUpdateOrderPacket(GameClientThread client, byte[] data) {
        super(client, data);

        int itemCount = readI();
        itemCount = Math.min(itemCount, LIMIT);
        orderList = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            int objectId = readI();
            int order = readI();
            orderList.add(new InventoryOrder(objectId, order));
        }

        handlePacket();
    }

    @Override
    public void handlePacket() {
        PlayerInstance player = client.getCurrentPlayer();
        if (player == null) {
            return;
        }

        getOrderList().forEach((inventoryOrder -> {
            player.getInventory().moveItemAndRecord(inventoryOrder.getObjectID(), inventoryOrder.getOrder());
        }));

        List<ItemInstance> items = player.getInventory().getUpdatedItems();

        InventoryUpdatePacket iu = new InventoryUpdatePacket(items);
        iu.writeMe();
        player.sendPacket(iu);

        player.getInventory().resetAndApplyUpdatedItems();
    }
}
