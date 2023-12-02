package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;

public class AuthRequest extends ClientPacket {

    private final String _username;

    public AuthRequest(byte[] data) {
        super(data);

        _username = readS();
    }

    public String getUsername() {
        return _username;
    }
}
