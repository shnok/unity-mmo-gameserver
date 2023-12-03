package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.model.Point3D;

public class ObjectDirectionPacket extends ServerPacket {
    public ObjectDirectionPacket(int id, float speed, Point3D direction) {
        super(ServerPacketType.ObjectMoveDirection.getValue());
        writeI(id);
        writeF(speed);
        writeF(direction.getX());
        writeF(direction.getY());
        writeF(direction.getZ());
        buildPacket();
    }
}