package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;

public class ObjectAnimation extends ServerPacket {

    public ObjectAnimation(int id, byte animId, float value) {
        super((byte) 0x08);
        writeI(id);
        writeB(animId);
        writeF(value);
        buildPacket();
    }
}