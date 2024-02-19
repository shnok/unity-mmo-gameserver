package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class ActionFailedPacket extends ServerPacket {
    public ActionFailedPacket(byte action) {
        super(ServerPacketType.ActionFailed.getValue());
        writeB(action);
        buildPacket();
    }
}
