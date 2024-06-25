package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class RemoveObjectPacket extends SendablePacket {
    public RemoveObjectPacket(int id) {
        super(ServerPacketType.RemoveObject.getValue());

        writeI(id);
        buildPacket();
    }
}