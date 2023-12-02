package com.shnok.javaserver.dto.serverpackets;

import com.shnok.javaserver.dto.ServerPacket;

public class AuthResponse extends ServerPacket {

    public AuthResponse(AuthResponseType reason) {
        super((byte) 0x01);
        writeB((byte) reason.ordinal());
        buildPacket();
    }

    public enum AuthResponseType {
        ALLOW,
        ALREADY_CONNECTED,
        INVALID_USERNAME
    }
}
