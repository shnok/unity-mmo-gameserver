package com.shnok.javaserver.dto.external.clientpackets.item;

import com.shnok.javaserver.dto.external.ClientPacket;
import com.shnok.javaserver.dto.external.serverpackets.item.InventoryItemListPacket;
import com.shnok.javaserver.thread.GameClientThread;

public class RequestInventoryOpenPacket extends ClientPacket {
    public RequestInventoryOpenPacket(GameClientThread client) {
        super(client, new byte[1]);

        handlePacket();
    }

    @Override
    public void handlePacket() {
        InventoryItemListPacket packet = new InventoryItemListPacket(client.getCurrentPlayer(), true);
        client.getCurrentPlayer().getInventory().getUpdatedItems();
        client.sendPacket(packet);
    }
}
