package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;

public class ActionAllowedPacket extends SendablePacket {
    public ActionAllowedPacket(byte action) {
        super(ServerPacketType.ActionAllowed.getValue());
        writeB(action);
        buildPacket();
    }
}