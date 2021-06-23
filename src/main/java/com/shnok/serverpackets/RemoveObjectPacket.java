package com.shnok.serverpackets;

public class RemoveObjectPacket extends ServerPacket {
    public RemoveObjectPacket(int id) {
        super((byte)0x06);

        writeI(id);
        buildPacket();
    }
}