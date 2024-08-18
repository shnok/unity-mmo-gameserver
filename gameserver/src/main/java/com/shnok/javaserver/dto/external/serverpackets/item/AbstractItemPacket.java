package com.shnok.javaserver.dto.external.serverpackets.item;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.model.item.ItemInfo;
import com.shnok.javaserver.model.object.ItemInstance;

import java.util.List;

public abstract class AbstractItemPacket extends SendablePacket {
    public AbstractItemPacket(byte type) {
        super(type);
    }

    protected void writeItem(ItemInstance item) {
        writeItem(new ItemInfo(item));
    }

    protected void writeItem(ItemInfo item) {
        writeI(item.getObjectId()); // ObjectId
        writeI(item.getItem().getId()); // ItemId
        writeB((byte) item.getLocation()); // Inventory? Warehouse?
        writeI(item.getSlot()); // Slot
        writeI((int) item.getCount()); // Quantity
        writeB(item.getCategory()); // Item Type 2 : 00-weapon, 01-shield/armor, 02-ring/earring/necklace, 03-questitem, 04-adena, 05-item
        writeB(item.getEquipped()); // Equipped : 00-No, 01-yes
        if(item.getCategory() <= 2) {
            writeI(item.getItem().getBodyPart().getValue());
        } else {
            writeI(0);
        }
        writeI(item.getEnchant()); // Enchant level
        writeL(item.getTime());
    }
}
