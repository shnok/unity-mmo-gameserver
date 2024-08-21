package com.shnok.javaserver.dto.external.serverpackets.authentication;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;

public class LeaveWorldPacket extends SendablePacket {
    public LeaveWorldPacket() {
        super(ServerPacketType.LeaveWorld.getValue());

        writeB((byte) 0);

        buildPacket();
    }
}