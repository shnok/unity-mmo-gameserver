package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class RequestUnEquipItemPacket extends ReceivablePacket {
    private final int slot;

    public RequestUnEquipItemPacket(byte[] data) {
        super(data);
        slot = readI();
    }
}
