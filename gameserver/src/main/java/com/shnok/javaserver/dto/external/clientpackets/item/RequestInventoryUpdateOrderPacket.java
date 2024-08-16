package com.shnok.javaserver.dto.external.clientpackets.item;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class RequestInventoryUpdateOrderPacket extends ReceivablePacket {

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

    public RequestInventoryUpdateOrderPacket(byte[] data) {
        super(data);

        int itemCount = readI();
        itemCount = Math.min(itemCount, LIMIT);
        orderList = new ArrayList<>(itemCount);
        for (int i = 0; i < itemCount; i++) {
            int objectId = readI();
            int order = readI();
            orderList.add(new InventoryOrder(objectId, order));
        }
    }
}
