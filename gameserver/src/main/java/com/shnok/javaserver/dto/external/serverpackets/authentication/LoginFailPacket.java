package com.shnok.javaserver.dto.external.serverpackets.authentication;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.LoginFailReason;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;

public class LoginFailPacket extends SendablePacket {
    public LoginFailPacket(LoginFailReason loginFailReason) {
        super(ServerPacketType.LoginFail.getValue());
        writeB((byte) loginFailReason.getCode());

        buildPacket();
    }
}

