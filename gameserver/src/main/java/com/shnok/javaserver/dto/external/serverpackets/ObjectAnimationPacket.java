package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;

public class ObjectAnimationPacket extends SendablePacket {

    public ObjectAnimationPacket(int id, byte animId, float value) {
        super(ServerPacketType.ObjectAnimation.getValue());
        writeI(id);
        writeB(animId);
        writeF(value);
        buildPacket();
    }
}