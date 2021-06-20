package com.shnok.clientpackets;

public class AuthRequest extends ClientPacket {

    private final String _username;

    public AuthRequest(byte[] data) {
        super(data);

        _username = readS(0);
    }

    public String getUsername() {
        return _username;
    }
}
