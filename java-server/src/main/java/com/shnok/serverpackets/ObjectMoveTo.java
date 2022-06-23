package com.shnok.serverpackets;

import com.shnok.model.Point3D;

public class ObjectMoveTo extends ServerPacket {
    private final int _id;
    private final Point3D _pos;

    public ObjectMoveTo(int id, Point3D pos) {
        super((byte) 0x0B);

        _id = id;
        _pos = pos;

        writeI(_id);
        writeF(_pos.getX());
        writeF(_pos.getY());
        writeF(_pos.getZ());
        buildPacket();
    }
}


