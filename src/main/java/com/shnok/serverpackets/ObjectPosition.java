package com.shnok.serverpackets;

import com.shnok.model.Point3D;

public class ObjectPosition extends ServerPacket {
    private int _id;
    private Point3D _pos;

    public ObjectPosition(int id, Point3D pos) {
        super((byte)0x05);

        _id = id;
        _pos = pos;

        writeI(_id);
        writeI(_pos.getX());
        writeI(_pos.getY());
        writeI(_pos.getZ());
        buildPacket();
    }
}
