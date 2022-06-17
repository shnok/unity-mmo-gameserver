package com.shnok.serverpackets;

public class ApplyDamage extends ServerPacket {
    public ApplyDamage(int sender, int target, byte attackType, int value) {
        super((byte)0x09);
        writeI(sender);
        writeI(target);
        writeB(attackType);
        writeI(value);

        buildPacket();
    }
}