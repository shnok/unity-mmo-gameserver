package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;

public class KeyPacket extends SendablePacket {
    public KeyPacket(byte[] key, boolean allowed) {
        super(ServerPacketType.Key.getValue());

        writeB((byte) key.length);
        writeB(key);
        writeB(allowed ? (byte) 1 : (byte) 0);

        buildPacket();
    }
}
