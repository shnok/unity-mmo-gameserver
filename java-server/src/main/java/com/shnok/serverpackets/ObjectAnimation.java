package com.shnok.serverpackets;

import com.shnok.model.Point3D;

public class ObjectAnimation extends ServerPacket {

    public ObjectAnimation(int id, byte animId, float value) {
        super((byte)0x08);
        writeI(id);
        writeB(animId);
        writeF(value);
        buildPacket();
    }
}