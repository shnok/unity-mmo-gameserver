package com.shnok.serverpackets;

public class ApplyDamage extends ServerPacket {
    public ApplyDamage(int sender, int target, byte attackId, int value) {
        super((byte)0x09);
        writeI(sender);
        writeI(target);
        writeB(attackId);
        writeI(value);

        buildPacket();
    }
}