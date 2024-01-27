package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.model.Point3D;

public class ObjectMoveToPacket extends ServerPacket {
    public ObjectMoveToPacket(int id, Point3D pos, float speed) {
        super(ServerPacketType.ObjectMoveTo.getValue());

        writeI(id);
        writeF(pos.getX());
        writeF(pos.getY());
        writeF(pos.getZ());
        writeF(speed);
        buildPacket();
    }
}


