package com.shnok.clientpackets;

public class RequestAttack extends ClientPacket {
    private int targetId;
    private byte attackId;
    private int damage;

    public RequestAttack(byte[] data) {
        super(data);
        targetId = readI();
        attackId = readB();
        damage = readI();
    }

    public int getTargetId() {
        return targetId;
    }

    public byte getAttackId() {
        return attackId;
    }

    public int getDamage() {
        return damage;
    }
}
