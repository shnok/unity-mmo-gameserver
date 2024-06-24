package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class AutoAttackStopPacket extends ServerPacket {
    public AutoAttackStopPacket(int id) {
        super(ServerPacketType.AutoAttackStop.getValue());
        writeI(id);
        buildPacket();
    }
}