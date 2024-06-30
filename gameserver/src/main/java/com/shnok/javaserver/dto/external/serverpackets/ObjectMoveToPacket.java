package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.Point3D;

public class ObjectMoveToPacket extends SendablePacket {
    public ObjectMoveToPacket(int id, Point3D pos, int speed, boolean walking) {
        super(ServerPacketType.ObjectMoveTo.getValue());

        writeI(id);
        writeF(pos.getX());
        writeF(pos.getY());
        writeF(pos.getZ());
        writeI(speed);
        writeB(walking ? (byte) 1 : (byte) 0);
        buildPacket();
    }
}


