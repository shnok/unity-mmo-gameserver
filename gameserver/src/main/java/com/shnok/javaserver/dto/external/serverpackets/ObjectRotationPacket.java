package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;

public class ObjectRotationPacket extends SendablePacket {
    public ObjectRotationPacket(int id, float angle) {
        super(ServerPacketType.ObjectRotation.getValue());
        writeI(id);
        writeF(angle);
        buildPacket();
    }
}
