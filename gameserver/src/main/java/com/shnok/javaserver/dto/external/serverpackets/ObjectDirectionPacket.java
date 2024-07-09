package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.Point3D;

public class ObjectDirectionPacket extends SendablePacket {
    public ObjectDirectionPacket(int id, float speed, Point3D direction) {
        super(ServerPacketType.ObjectMoveDirection.getValue());
        writeI(id);
        writeI((int) speed);
        writeF(direction.getX());
        writeF(direction.getY());
        writeF(direction.getZ());
        buildPacket();
    }
}