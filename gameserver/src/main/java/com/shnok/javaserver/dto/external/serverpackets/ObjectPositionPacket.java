package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.Point3D;

public class ObjectPositionPacket extends SendablePacket {
    public ObjectPositionPacket(int id, Point3D pos) {
        super(ServerPacketType.ObjectPosition.getValue());

        writeI(id);
        writeF(pos.getX());
        writeF(pos.getY());
        writeF(pos.getZ());
        buildPacket();
    }
}
