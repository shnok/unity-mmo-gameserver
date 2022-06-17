package com.shnok.clientpackets;

public class RequestAttack extends ClientPacket {
    private int targetId;
    private byte attackType;
    public RequestAttack(byte[] data) {
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
