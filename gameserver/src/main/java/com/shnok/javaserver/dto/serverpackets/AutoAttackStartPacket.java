package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class AutoAttackStartPacket extends ServerPacket {
    public AutoAttackStartPacket(int id) {
        super(ServerPacketType.AutoAttackStart.getValue());
        writeI(id);
        buildPacket();
    }
}
