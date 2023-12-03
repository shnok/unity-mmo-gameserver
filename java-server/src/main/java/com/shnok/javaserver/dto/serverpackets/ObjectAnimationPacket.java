package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class ObjectAnimationPacket extends ServerPacket {

    public ObjectAnimationPacket(int id, byte animId, float value) {
        super(ServerPacketType.ObjectAnimation.getValue());
        writeI(id);
        writeB(animId);
        writeF(value);
        buildPacket();
    }
}