package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;
import com.shnok.javaserver.model.Point3D;

public class ObjectMoveToPacket extends ServerPacket {
    private final int _id;
    private final Point3D _pos;

    public ObjectMoveToPacket(int id, Point3D pos) {
        super(ServerPacketType.ObjectMoveTo.getValue());

        _id = id;
        _pos = pos;

        writeI(_id);
        writeF(_pos.getX());
        writeF(_pos.getY());
        writeF(_pos.getZ());
        buildPacket();
    }
}


