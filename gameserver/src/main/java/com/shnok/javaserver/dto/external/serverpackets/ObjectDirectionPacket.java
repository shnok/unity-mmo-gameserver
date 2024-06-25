package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.ServerPacketType;
import com.shnok.javaserver.model.Point3D;

public class ObjectDirectionPacket extends SendablePacket {
    public ObjectDirectionPacket(int id, int speed, Point3D direction) {
        super(ServerPacketType.ObjectMoveDirection.getValue());
        writeI(id);
        writeI(speed);
        writeF(direction.getX());
        writeF(direction.getY());
        writeF(direction.getZ());
        buildPacket();
    }
}