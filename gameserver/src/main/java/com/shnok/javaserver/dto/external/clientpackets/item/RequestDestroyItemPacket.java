package com.shnok.javaserver.dto.external.clientpackets.item;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class RequestDestroyItemPacket extends ReceivablePacket {
    private final int itemObjectId;
    private final int count;

    public RequestDestroyItemPacket(byte[] data) {
        super(data);
        itemObjectId = readI();
        count = readI();
    }
}