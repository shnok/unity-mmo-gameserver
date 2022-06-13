package com.shnok.serverpackets;

public class ObjectRotation extends ServerPacket {
    public ObjectRotation(int id, float angle) {
        super((byte)0x07);
        writeI(id);
        writeF(angle);
        buildPacket();
    }
}
