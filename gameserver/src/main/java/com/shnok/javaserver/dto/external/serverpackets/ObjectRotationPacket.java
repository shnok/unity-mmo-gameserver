package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class ObjectRotationPacket extends SendablePacket {
    public ObjectRotationPacket(int id, float angle) {
        super(ServerPacketType.ObjectRotation.getValue());
        writeI(id);
        writeF(angle);
        buildPacket();
    }
}
