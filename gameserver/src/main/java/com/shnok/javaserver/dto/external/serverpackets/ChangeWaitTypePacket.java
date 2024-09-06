package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.model.object.entity.PlayerInstance;

public class ChangeWaitTypePacket extends SendablePacket {

    public static final int WT_SITTING = 0;
    public static final int WT_STANDING = 1;
    public static final int WT_START_FAKEDEATH = 2;
    public static final int WT_STOP_FAKEDEATH = 3;

    public ChangeWaitTypePacket(PlayerInstance owner, int newMoveType) {
        super(ServerPacketType.ChangeWaitType.getValue());

        if (owner == null) {
            return;
        }

        writeI(owner.getId());
        writeI(newMoveType);
        writeF(owner.getPosX());
        writeF(owner.getPosY());
        writeF(owner.getPosZ());
    }
}