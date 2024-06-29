package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.external.ServerPacketType;

public class ActionFailedPacket extends SendablePacket {
    public ActionFailedPacket(byte action) {
        super(ServerPacketType.ActionFailed.getValue());
        writeB(action);
        buildPacket();
    }
}
