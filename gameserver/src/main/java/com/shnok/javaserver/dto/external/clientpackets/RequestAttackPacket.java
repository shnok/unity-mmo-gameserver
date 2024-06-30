package com.shnok.javaserver.dto.external.clientpackets;

import com.shnok.javaserver.dto.ReceivablePacket;
import lombok.Getter;

@Getter
public class RequestAttackPacket extends ReceivablePacket {
    private final int targetId;
    private final byte attackType;
    public RequestAttackPacket(byte[] data) {
        super(data);
        targetId = readI();
        attackType = readB();
    }
}
