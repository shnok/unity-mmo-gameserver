package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.model.Point3D;

public class ObjectPositionPacket extends ServerPacket {
    public ObjectPositionPacket(int id, Point3D pos) {
        super(ServerPacketType.ObjectPosition.getValue());

        writeI(id);
        writeF(pos.getX());
        writeF(pos.getY());
        writeF(pos.getZ());
        buildPacket();
    }
}
