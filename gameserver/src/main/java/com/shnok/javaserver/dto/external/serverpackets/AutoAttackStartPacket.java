package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;

public class AutoAttackStartPacket extends SendablePacket {
    public AutoAttackStartPacket(int id) {
        super(ServerPacketType.AutoAttackStart.getValue());
        writeI(id);
        buildPacket();
    }
}
