package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class ApplyDamagePacket extends ServerPacket {
    public ApplyDamagePacket(int sender, int target, byte attackType, int value, boolean criticalHit) {
        super(ServerPacketType.ApplyDamage.getValue());
        writeI(sender);
        writeI(target);
        writeB(attackType);
        writeI(value);
        writeB(criticalHit ? (byte) 1 : (byte) 0);

        buildPacket();
    }
}