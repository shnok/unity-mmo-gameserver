package com.shnok.serverpackets;

import com.shnok.model.Point3D;

public class ObjectAnimation extends ServerPacket {

    public ObjectAnimation(int id, int animId, float value) {
        super((byte)0x08);
        writeI(id);
        writeI(animId);
        writeF(value);
        buildPacket();
    }
}