package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class EntitySetTargetPacket extends SendablePacket {
    public EntitySetTargetPacket(int id, int targetId) {
        super(ServerPacketType.EntitySetTarget.getValue());
        writeI(id);
        writeI(targetId);
        buildPacket();
    }
}
