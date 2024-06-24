package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class RemoveObjectPacket extends ServerPacket {
    public RemoveObjectPacket(int id) {
        super(ServerPacketType.RemoveObject.getValue());

        writeI(id);
        buildPacket();
    }
}