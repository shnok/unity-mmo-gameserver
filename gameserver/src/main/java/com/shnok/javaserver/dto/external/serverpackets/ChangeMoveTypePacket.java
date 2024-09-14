package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.object.MovableObject;

public class ChangeMoveTypePacket extends SendablePacket {

    public static final int WALK = 0;
    public static final int RUN = 1;

    public ChangeMoveTypePacket(MovableObject obj) {
        super(ServerPacketType.ChangeMoveType.getValue());
        writeI(obj.getId());
        writeI(obj.isRunning() ? RUN : WALK);
        buildPacket();
    }
}