package com.shnok.javaserver.dto.external.serverpackets.authentication;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;

public class RestartResponsePacket extends SendablePacket {

    public RestartResponsePacket() {
        super(ServerPacketType.RestartResponse.getValue());

        writeI((byte) 1);
        writeS("Ora ora!");

        buildPacket();
    }
}