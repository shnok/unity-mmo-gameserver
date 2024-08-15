package com.shnok.javaserver.dto.external.clientpackets.item;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class UseItemPacket extends ReceivablePacket {
    private final int itemObjectId;

    public UseItemPacket(byte[] data) {
        super(data);
        itemObjectId = readI();
    }
}
