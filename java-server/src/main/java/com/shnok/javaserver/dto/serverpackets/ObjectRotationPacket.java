package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class ObjectRotationPacket extends ServerPacket {
    public ObjectRotationPacket(int id, float angle) {
        super(ServerPacketType.ObjectRotation.getValue());
        writeI(id);
        writeF(angle);
        buildPacket();
    }
}
