package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;

public class RequestAutoAttackPacket extends ReceivablePacket {
    public RequestAutoAttackPacket(byte[] data) {
        super(data);
    }
}