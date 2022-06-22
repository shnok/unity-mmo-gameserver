package com.shnok.serverpackets;

public class RemoveObject extends ServerPacket {
    public RemoveObject(int id) {
        super((byte) 0x06);

        writeI(id);
        buildPacket();
    }
}