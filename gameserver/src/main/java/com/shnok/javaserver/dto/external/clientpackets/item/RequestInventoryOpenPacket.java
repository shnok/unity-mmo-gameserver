package com.shnok.javaserver.dto.external.clientpackets.item;

import com.shnok.javaserver.dto.ReceivablePacket;

public class RequestInventoryOpenPacket extends ReceivablePacket {
    public RequestInventoryOpenPacket(byte[] data) {
        super(data);
        readB();
    }
}
