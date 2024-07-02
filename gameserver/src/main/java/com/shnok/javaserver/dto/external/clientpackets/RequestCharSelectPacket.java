package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class RequestCharSelectPacket extends ReceivablePacket {
    private final int charSlot;
    public RequestCharSelectPacket(byte[] data) {
        super(data);

        charSlot = readB();
    }
}
