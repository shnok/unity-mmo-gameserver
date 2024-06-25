package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class AutoAttackStopPacket extends SendablePacket {
    public AutoAttackStopPacket(int id) {
        super(ServerPacketType.AutoAttackStop.getValue());
        writeI(id);
        buildPacket();
    }
}