package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;

public class RemoveObject extends ServerPacket {
    public RemoveObject(int id) {
        super((byte) 0x06);

        writeI(id);
        buildPacket();
    }
}