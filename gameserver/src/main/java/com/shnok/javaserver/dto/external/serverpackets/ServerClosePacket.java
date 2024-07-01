package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;


public class ServerClosePacket extends SendablePacket {
    // Kick user
    public ServerClosePacket() {
        super(ServerPacketType.ServerClose.getValue());

        buildPacket();
    }
}
