package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;
import lombok.Getter;

@Getter
public class RequestAttackPacket extends ClientPacket {
    private final int targetId;
    private final byte attackType;
    public RequestAttackPacket(byte[] data) {
        super(data);
        targetId = readI();
        attackType = readB();
    }
}
