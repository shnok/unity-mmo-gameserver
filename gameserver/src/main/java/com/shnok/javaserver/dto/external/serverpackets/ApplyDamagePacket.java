package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.ServerPacketType;

public class ApplyDamagePacket extends SendablePacket {
    public ApplyDamagePacket(int sender, int target, int damage, int newHp, boolean criticalHit) {
        super(ServerPacketType.ApplyDamage.getValue());
        writeI(sender);
        writeI(target);
        writeI(damage);
        writeI(newHp);
        writeB(criticalHit ? (byte) 1 : (byte) 0);
        buildPacket();
    }
}