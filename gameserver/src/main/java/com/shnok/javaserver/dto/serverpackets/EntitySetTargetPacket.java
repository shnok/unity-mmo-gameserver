package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.enums.ServerPacketType;

public class EntitySetTargetPacket extends ServerPacket {
    public EntitySetTargetPacket(int id, int targetId) {
        super(ServerPacketType.EntitySetTarget.getValue());
        writeI(id);
        writeI(targetId);
        buildPacket();
    }
}
