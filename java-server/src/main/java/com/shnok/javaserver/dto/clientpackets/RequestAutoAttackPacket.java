package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;

public class RequestAutoAttackPacket extends ClientPacket {
    public RequestAutoAttackPacket(byte[] data) {
        super(data);
    }
}