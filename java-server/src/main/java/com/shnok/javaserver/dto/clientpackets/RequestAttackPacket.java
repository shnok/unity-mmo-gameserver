package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;

public class RequestAttackPacket extends ClientPacket {
    private int targetId;
    private byte attackType;
    public RequestAttackPacket(byte[] data) {
        super(data);
        targetId = readI();
        attackType = readB();
    }

    public int getTargetId() {
        return targetId;
    }

    public byte getAttackType() {
        return attackType;
    }
}
