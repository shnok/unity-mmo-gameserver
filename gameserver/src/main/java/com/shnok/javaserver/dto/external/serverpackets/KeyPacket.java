package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.packettypes.external.ServerPacketType;

public class KeyPacket extends SendablePacket {
    public KeyPacket(byte[] key, int id) {
        super(ServerPacketType.Key.getValue());

        writeB((byte) key.length);
        writeB(key);
        writeI(id);

        buildPacket();
    }
}
